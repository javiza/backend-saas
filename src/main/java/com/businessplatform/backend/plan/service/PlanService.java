package com.businessplatform.backend.plan.service;

import com.businessplatform.backend.application.entity.Application;
import com.businessplatform.backend.application.repository.ApplicationRepository;
import com.businessplatform.backend.plan.dto.CreatePlanRequest;
import com.businessplatform.backend.plan.dto.PlanResponse;
import com.businessplatform.backend.plan.entity.Plan;
import com.businessplatform.backend.plan.repository.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final ApplicationRepository applicationRepository;

    public PlanService(PlanRepository planRepository, ApplicationRepository applicationRepository) {
        this.planRepository = planRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public PlanResponse create(CreatePlanRequest request) {
        if (planRepository.existsByCode(request.code())) {
            throw new PlanAlreadyExistsException(
                    "Ya existe un plan con el código: " + request.code()
            );
        }

        Plan plan = new Plan();
        plan.setCode(request.code());
        applyRequest(plan, request);

        return PlanResponse.from(planRepository.save(plan));
    }

    @Transactional
    public PlanResponse update(UUID id, CreatePlanRequest request) {
        Plan plan = findEntity(id);

        if (planRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new PlanAlreadyExistsException(
                    "Ya existe un plan con el código: " + request.code()
            );
        }

        plan.setCode(request.code());
        applyRequest(plan, request);

        return PlanResponse.from(plan);
    }

    private void applyRequest(Plan plan, CreatePlanRequest request) {
        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setPrice(request.price());
        plan.setBillingCycle(request.billingCycle());
        plan.setTrialDays(request.trialDays() == null ? 0 : request.trialDays());

        Set<Application> applications = new HashSet<>(
                applicationRepository.findAllById(request.applicationIds())
        );

        if (applications.size() != new HashSet<>(request.applicationIds()).size()) {
            throw new RuntimeException("Alguna de las aplicaciones indicadas no existe");
        }

        plan.setApplications(applications);
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> findAll() {
        return planRepository.findAll()
                .stream()
                .map(PlanResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlanResponse findById(UUID id) {
        return PlanResponse.from(findEntity(id));
    }

    @Transactional
    public PlanResponse setActive(UUID id, boolean active) {
        Plan plan = findEntity(id);
        plan.setActive(active);
        return PlanResponse.from(plan);
    }

    @Transactional(readOnly = true)
    public Plan findEntity(UUID id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new PlanNotFoundException("Plan no encontrado: " + id));
    }
}
