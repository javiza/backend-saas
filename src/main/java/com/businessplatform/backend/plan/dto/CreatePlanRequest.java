package com.businessplatform.backend.plan.dto;

import com.businessplatform.backend.plan.entity.BillingCycle;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreatePlanRequest(
        @NotBlank(message = "El código del plan es obligatorio")
        @Size(max = 50, message = "El código no puede superar 50 caracteres")
        String code,

        @NotBlank(message = "El nombre del plan es obligatorio")
        String name,

        @NotBlank(message = "La descripción del plan es obligatoria")
        String description,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
        BigDecimal price,

        @NotNull(message = "El ciclo de facturación es obligatorio")
        BillingCycle billingCycle,

        @Min(value = 0, message = "Los días de prueba no pueden ser negativos")
        Integer trialDays,

        @NotEmpty(message = "El plan debe incluir al menos una aplicación")
        List<UUID> applicationIds
) {
}
