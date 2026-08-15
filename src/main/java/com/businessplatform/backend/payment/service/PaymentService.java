package com.businessplatform.backend.payment.service;

import com.businessplatform.backend.company.entity.Company;
import com.businessplatform.backend.payment.client.TransbankOneclickClient;
import com.businessplatform.backend.payment.client.dto.ChargeDetailResult;
import com.businessplatform.backend.payment.client.dto.ChargeResult;
import com.businessplatform.backend.payment.dto.PaymentResponse;
import com.businessplatform.backend.payment.entity.Payment;
import com.businessplatform.backend.payment.entity.PaymentStatus;
import com.businessplatform.backend.payment.repository.PaymentRepository;
import com.businessplatform.backend.paymentmethod.entity.PaymentMethod;
import com.businessplatform.backend.paymentmethod.repository.PaymentMethodRepository;
import com.businessplatform.backend.companysubscription.entity.CompanySubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Cobra a la tarjeta guardada de una empresa (vía Oneclick) y deja
 * registro en "payments". Lo usa CompanySubscriptionService tanto al
 * contratar un plan sin trial como al renovar automáticamente por cron.
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final TransbankOneclickClient transbankClient;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentMethodRepository paymentMethodRepository,
            TransbankOneclickClient transbankClient
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.transbankClient = transbankClient;
    }

    /**
     * Cobra "amount" a la tarjeta de la empresa dueña de la suscripción.
     * Lanza NoPaymentMethodException si no tiene tarjeta, o
     * PaymentDeclinedException si Transbank rechaza el cobro (en ambos
     * casos ya queda registrado el intento fallido cuando corresponde,
     * para tener historial).
     */
    @Transactional
    public Payment charge(CompanySubscription subscription, BigDecimal amount) {
        Company company = subscription.getCompany();

        PaymentMethod paymentMethod = paymentMethodRepository.findByCompanyId(company.getId())
                .orElseThrow(() -> new NoPaymentMethodException(
                        "La empresa \"" + company.getName() + "\" no tiene una tarjeta registrada"
                ));

        // Un buyOrder por suscripción+momento: evita choques con el
        // uk_payments_buy_order si se reintenta el cobro de la misma renovación.
        String buyOrder = subscription.getId().toString().replace("-", "") + "-" + System.currentTimeMillis();

        ChargeResult result;
        try {
            result = transbankClient.charge(paymentMethod.getUsername(), paymentMethod.getTbkUser(), buyOrder, amount);
        } catch (RestClientException e) {
            log.warn("Fallo de comunicación con Transbank al cobrar suscripción {}: {}", subscription.getId(), e.getMessage());
            Payment failed = new Payment(subscription, buyOrder, amount, PaymentStatus.REJECTED, null, null);
            paymentRepository.save(failed);
            throw new PaymentDeclinedException("No se pudo contactar a Transbank para procesar el cobro");
        }

        ChargeDetailResult detail = result.firstDetail();
        boolean approved = detail != null && detail.approved();

        Payment payment = new Payment(
                subscription,
                buyOrder,
                amount,
                approved ? PaymentStatus.AUTHORIZED : PaymentStatus.REJECTED,
                detail == null ? null : detail.authorizationCode(),
                detail == null ? null : detail.responseCode()
        );
        payment = paymentRepository.save(payment);

        if (!approved) {
            throw new PaymentDeclinedException(
                    "Transbank rechazó el cobro" + (detail == null ? "" : " (código " + detail.responseCode() + ")")
            );
        }

        return payment;
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByCompany(UUID companyId) {
        return paymentRepository.findByCompanySubscriptionCompanyIdOrderByCreatedAtDesc(companyId)
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
