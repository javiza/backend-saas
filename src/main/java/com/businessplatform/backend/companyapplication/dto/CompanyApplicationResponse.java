package com.businessplatform.backend.companyapplication.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyApplicationResponse(
        UUID id,
        UUID companyId,
        String companyName,
        UUID applicationId,
        String applicationCode,
        String applicationName,
        boolean active,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
}
