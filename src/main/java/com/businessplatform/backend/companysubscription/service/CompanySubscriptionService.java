package com.businessplatform.backend.companysubscription.service;

import com.businessplatform.backend.application.entity.Application;
import com.businessplatform.backend.company.entity.Company;
import com.businessplatform.backend.company.repository.CompanyRepository;
import com.businessplatform.backend.company.service.CompanyNotFoundException;
import com.businessplatform.backend.companyapplication.entity.CompanyApplication;
import com.businessplatform.backend.companyapplication.repository.CompanyApplicationRepository;
import com.businessplatform.backend.companysubscription.dto.CompanySubscriptionResponse;
import com.businessplatform.backend.companysubscription.entity.CompanySubscription;
import com.businessplatform.backend.companysubscription.entity.SubscriptionStatus;
import com.businessplatform.backend.companysubscription.repository.CompanySubscriptionRepository;
import com.businessplatform.backend.payment.service.NoPaymentMethodException;
import com.businessplatform.backend.payment.service.PaymentDeclinedException;
import com.businessplatform.backend.payment.service.PaymentService;
import com.businessplatform.backend.paymentmethod.repository.PaymentMethodRepository;
import com.businessplatform.backend.plan.entity.Plan;
import com.businessplatform.backend.plan.repository.PlanRepository;
import com.businessplatform.backend.plan.service.PlanNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Contratación de planes: requiere que la empresa tenga una tarjeta
 * guardada (ver PaymentMethodService); si el plan no tiene trial, cobra
 * de inmediato. Arranca en TRIAL si el plan tiene días de prueba (o
 * directamente ACTIVE si no), provisiona el acceso a las apps del plan,
 * y en cada vencimiento intenta cobrar la renovación automáticamente
 * (ver renewOrExpireDueSubscriptions, corre por cron); si no hay tarjeta
 * o Transbank rechaza el cobro, corta el acceso igual que antes.
 */
@Service
public class CompanySubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(CompanySubscriptionService.class);

    private static final List<SubscriptionStatus> LIVE_STATUSES =
            List.of(SubscriptionStatus.TRIAL, SubscriptionStatus.ACTIVE);

    private final CompanySubscriptionRepository subscriptionRepository;
    private final CompanyRepository companyRepository;
    private final PlanRepository planRepository;
    private final CompanyApplicationRepository companyApplicationRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentService paymentService;

    public CompanySubscriptionService(
            CompanySubscriptionRepository subscriptionRepository,
            CompanyRepository companyRepository,
            PlanRepository planRepository,
            CompanyApplicationRepository companyApplicationRepository,
            PaymentMethodRepository paymentMethodRepository,
            PaymentService paymentService
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.companyRepository = companyRepository;
        this.planRepository = planRepository;
        this.companyApplicationRepository = companyApplicationRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public CompanySubscriptionResponse subscribe(UUID companyId, UUID planId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Empresa no encontrada: " + companyId));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("Plan no encontrado: " + planId));

        if (!plan.isActive()) {
            throw new CompanySubscriptionConflictException(
                    "El plan \"" + plan.getName() + "\" no está disponible actualmente"
            );
        }

        if (subscriptionRepository.existsByCompanyIdAndPlanIdAndStatusIn(companyId, planId, LIVE_STATUSES)) {
            throw new CompanySubscriptionConflictException(
                    "La empresa \"" + company.getName() + "\" ya tiene contratado el plan \"" + plan.getName() + "\""
            );
        }

        // Toda contratación requiere una tarjeta registrada, aunque el
        // plan arranque en trial: la vamos a necesitar para cobrar la
        // renovación cuando el trial (o el período pago) venza.
        if (!paymentMethodRepository.existsByCompanyId(companyId)) {
            throw new NoPaymentMethodException(
                    "La empresa \"" + company.getName() + "\" debe registrar una tarjeta antes de contratar un plan"
            );
        }

        LocalDateTime now = LocalDateTime.now();
        boolean startsOnTrial = plan.getTrialDays() > 0;

        CompanySubscription subscription = new CompanySubscription();
        subscription.setCompany(company);
        subscription.setPlan(plan);
        subscription.setStartedAt(now);

        if (startsOnTrial) {
            LocalDateTime trialEndsAt = now.plusDays(plan.getTrialDays());
            subscription.setStatus(SubscriptionStatus.TRIAL);
            subscription.setTrialEndsAt(trialEndsAt);
            subscription.setCurrentPeriodEnd(trialEndsAt);
        } else {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setCurrentPeriodEnd(computePeriodEnd(now, plan));
        }

        subscription = subscriptionRepository.save(subscription);

        // Sin trial: se cobra de inmediato. Si Transbank rechaza el cobro,
        // la excepción revierte toda la transacción (no queda suscripción
        // ni acceso provisionado).
        if (!startsOnTrial) {
            paymentService.charge(subscription, plan.getPrice());
        }

        provisionAccess(company, plan, subscription.getCurrentPeriodEnd());

        return CompanySubscriptionResponse.from(subscription);
    }

    @Transactional
    public CompanySubscriptionResponse cancel(UUID companyId, UUID subscriptionId) {
        CompanySubscription subscription = subscriptionRepository.findById(subscriptionId)
                .filter(s -> s.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new CompanySubscriptionNotFoundException(
                        "Suscripción no encontrada para esta empresa: " + subscriptionId
                ));

        if (!LIVE_STATUSES.contains(subscription.getStatus())) {
            throw new CompanySubscriptionConflictException(
                    "Esta suscripción ya está " + subscription.getStatus().name().toLowerCase()
            );
        }

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setCanceledAt(LocalDateTime.now());

        revokeAccessIfNotCoveredElsewhere(subscription.getCompany(), subscription.getPlan());

        return CompanySubscriptionResponse.from(subscription);
    }

    @Transactional(readOnly = true)
    public List<CompanySubscriptionResponse> findByCompany(UUID companyId) {
        return subscriptionRepository.findByCompanyIdOrderByCreatedAtDesc(companyId)
                .stream()
                .map(CompanySubscriptionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompanySubscriptionResponse> findAll(SubscriptionStatus status) {
        List<CompanySubscription> subs = status == null
                ? subscriptionRepository.findAll()
                : subscriptionRepository.findByStatusIn(List.of(status));

        return subs.stream().map(CompanySubscriptionResponse::from).toList();
    }

    // Trials que vencen en las próximas 48hs: para que el admin las vea
    // en el panel y pueda ofrecer upgrade antes de que se corte el acceso.
    @Transactional(readOnly = true)
    public List<CompanySubscriptionResponse> findTrialsEndingSoon() {
        LocalDateTime now = LocalDateTime.now();
        return subscriptionRepository.findTrialsEndingBefore(SubscriptionStatus.TRIAL, now, now.plusHours(48))
                .stream()
                .map(CompanySubscriptionResponse::from)
                .toList();
    }

    /**
     * Corre por cron: para cada suscripción vencida (trial o período
     * pago) intenta cobrar la renovación a la tarjeta guardada.
     *   - Si cobra bien: extiende el período, deja/pasa la suscripción a
     *     ACTIVE y renueva el acceso a las apps del plan.
     *   - Si no hay tarjeta, Transbank rechaza el cobro, o el plan es de
     *     pago único (UNICO, sin renovación): pasa a EXPIRED y corta el
     *     acceso, igual que antes.
     */
    @Scheduled(cron = "0 0 * * * *") // cada hora, en punto
    @Transactional
    public void renewOrExpireDueSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<CompanySubscription> due = subscriptionRepository.findExpired(LIVE_STATUSES, now);

        for (CompanySubscription subscription : due) {
            Plan plan = subscription.getPlan();

            if (plan.getBillingCycle() == com.businessplatform.backend.plan.entity.BillingCycle.UNICO) {
                expireSubscription(subscription);
                continue;
            }

            try {
                paymentService.charge(subscription, plan.getPrice());

                LocalDateTime newPeriodEnd = computePeriodEnd(now, plan);
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setCurrentPeriodEnd(newPeriodEnd);
                provisionAccess(subscription.getCompany(), plan, newPeriodEnd);

                log.info(
                        "Suscripción renovada: empresa={}, plan={}, hasta={}",
                        subscription.getCompany().getName(),
                        plan.getCode(),
                        newPeriodEnd
                );
            } catch (NoPaymentMethodException | PaymentDeclinedException e) {
                log.warn(
                        "No se pudo renovar la suscripción de \"{}\" al plan \"{}\": {}",
                        subscription.getCompany().getName(),
                        plan.getCode(),
                        e.getMessage()
                );
                expireSubscription(subscription);
            }
        }
    }

    private void expireSubscription(CompanySubscription subscription) {
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        revokeAccessIfNotCoveredElsewhere(subscription.getCompany(), subscription.getPlan());
        log.info(
                "Suscripción vencida: empresa={}, plan={}",
                subscription.getCompany().getName(),
                subscription.getPlan().getCode()
        );
    }

    private void provisionAccess(Company company, Plan plan, LocalDateTime expiresAt) {
        for (Application app : plan.getApplications()) {
            CompanyApplication companyApplication = companyApplicationRepository
                    .findByCompanyIdAndApplicationId(company.getId(), app.getId())
                    .orElseGet(() -> new CompanyApplication(company, app));

            companyApplication.setActive(true);
            companyApplication.setExpiresAt(expiresAt);
            companyApplicationRepository.save(companyApplication);
        }
    }

    private void revokeAccessIfNotCoveredElsewhere(Company company, Plan revokedPlan) {
        List<CompanySubscription> stillLive = subscriptionRepository
                .findByCompanyIdOrderByCreatedAtDesc(company.getId())
                .stream()
                .filter(s -> LIVE_STATUSES.contains(s.getStatus()))
                .toList();

        Set<UUID> stillCoveredAppIds = stillLive.stream()
                .flatMap(s -> s.getPlan().getApplications().stream())
                .map(Application::getId)
                .collect(Collectors.toSet());

        for (Application app : revokedPlan.getApplications()) {
            if (stillCoveredAppIds.contains(app.getId())) {
                continue; // otra suscripción viva sigue dando acceso a esta app
            }
            companyApplicationRepository
                    .findByCompanyIdAndApplicationId(company.getId(), app.getId())
                    .ifPresent(ca -> ca.setActive(false));
        }
    }

    private LocalDateTime computePeriodEnd(LocalDateTime now, Plan plan) {
        return switch (plan.getBillingCycle()) {
            case MENSUAL -> now.plusMonths(1);
            case ANUAL -> now.plusYears(1);
            case UNICO -> null; // pago único: sin vencimiento automático
        };
    }
}
