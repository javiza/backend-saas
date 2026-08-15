package com.businessplatform.backend.companysubscription.dto;

import com.businessplatform.backend.companysubscription.entity.CompanySubscription;
import com.businessplatform.backend.companysubscription.entity.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanySubscriptionResponse(
        UUID id,
        UUID companyId,
        String companyName,
        UUID planId,
        String planCode,
        String planName,
        SubscriptionStatus status,
        LocalDateTime startedAt,
        LocalDateTime trialEndsAt,
        LocalDateTime currentPeriodEnd,
        LocalDateTime canceledAt,
        LocalDateTime createdAt
) {
    public static CompanySubscriptionResponse from(CompanySubscription s) {
        return new CompanySubscriptionResponse(
                s.getId(),
                s.getCompany().getId(),
                s.getCompany().getName(),
                s.getPlan().getId(),
                s.getPlan().getCode(),
                s.getPlan().getName(),
                s.getStatus(),
                s.getStartedAt(),
                s.getTrialEndsAt(),
                s.getCurrentPeriodEnd(),
                s.getCanceledAt(),
                s.getCreatedAt()
        );
    }
}
