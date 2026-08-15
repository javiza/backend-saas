package com.businessplatform.backend.auth.dto;

import com.businessplatform.backend.user.entity.Role;

import java.util.UUID;

public record LoginResponse(
        String token,
        UUID userId,
        String name,
        String email,
        Role role,
        UUID companyId
) {
}
