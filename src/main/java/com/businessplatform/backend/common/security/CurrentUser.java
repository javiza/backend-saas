package com.businessplatform.backend.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * JwtAuthenticationFilter deja el companyId del usuario en
 * Authentication#getDetails() y el rol como authority "ROLE_X".
 * Esta clase centraliza la lectura de esos datos: antes cada
 * controller que necesitaba "¿es admin? ¿de qué empresa es?" hubiera
 * tenido que repetir este mismo casteo/parsing.
 */
@Component
public class CurrentUser {

    public UUID companyId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getDetails() == null) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        return UUID.fromString((String) auth.getDetails());
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Lanza si el usuario no es ADMIN de plataforma y tampoco pertenece
     * a la empresa que está intentando consultar/modificar.
     */
    public void requireAdminOrOwnCompany(UUID companyId) {
        if (isAdmin()) {
            return;
        }
        if (!companyId().equals(companyId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes acceso a los datos de esta empresa"
            );
        }
    }
}
