package com.businessplatform.backend.paymentmethod.dto;

import jakarta.validation.constraints.NotBlank;

// token_ws recibido por POST en el response_url configurado.
public record FinishInscriptionRequest(
        @NotBlank(message = "El token es obligatorio")
        String token
) {
}
