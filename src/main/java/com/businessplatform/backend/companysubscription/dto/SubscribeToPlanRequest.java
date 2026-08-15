package com.businessplatform.backend.companysubscription.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubscribeToPlanRequest(
        @NotNull(message = "El id del plan es obligatorio")
        UUID planId
) {
}
