package com.businessplatform.backend.plan.dto;

import com.businessplatform.backend.plan.entity.BillingCycle;
import com.businessplatform.backend.plan.entity.Plan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PlanResponse(
        UUID id,
        String code,
        String name,
        String description,
        BigDecimal price,
        BillingCycle billingCycle,
        int trialDays,
        boolean active,
        List<PlanApplicationSummary> applications,
        LocalDateTime createdAt
) {
    public static PlanResponse from(Plan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getCode(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getBillingCycle(),
                plan.getTrialDays(),
                plan.isActive(),
                plan.getApplications().stream()
                        .map(app -> new PlanApplicationSummary(app.getId(), app.getCode(), app.getName()))
                        .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                        .toList(),
                plan.getCreatedAt()
        );
    }
}
