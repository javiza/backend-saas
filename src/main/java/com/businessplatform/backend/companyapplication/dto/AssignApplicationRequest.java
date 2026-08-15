package com.businessplatform.backend.companyapplication.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssignApplicationRequest(
        @NotNull(message = "El id de la aplicación es obligatorio")
        UUID applicationId,

        // Opcional: fecha de expiración de la licencia/suscripción.
        // Null = sin vencimiento.
        LocalDateTime expiresAt
) {
}
