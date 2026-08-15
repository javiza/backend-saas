package com.businessplatform.backend.user.dto;

import com.businessplatform.backend.user.entity.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Role role,
        UUID companyId,
        boolean active,
        LocalDateTime createdAt
) {
}
