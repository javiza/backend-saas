package com.businessplatform.backend.companyapplication.dto;

/**
 * Esto es lo que agencia_turismo o control_acceso consultan para saber
 * si deben dejar entrar a una empresa/usuario o mostrar un mensaje de
 * "suscripción inactiva".
 */
public record AccessCheckResponse(
        boolean hasAccess,
        String reason
) {
    public static AccessCheckResponse granted() {
        return new AccessCheckResponse(true, "OK");
    }

    public static AccessCheckResponse denied(String reason) {
        return new AccessCheckResponse(false, reason);
    }
}
