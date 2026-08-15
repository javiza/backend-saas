package com.businessplatform.backend.plan.dto;

import java.util.UUID;

public record PlanApplicationSummary(
        UUID id,
        String code,
        String name
) {
}
