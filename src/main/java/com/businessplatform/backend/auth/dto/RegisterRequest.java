package com.businessplatform.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Registro público de un cliente nuevo: crea su empresa y, dentro de
// ella, el usuario que la administra (rol MANAGER). No se expone
// companyId ni role acá a propósito: eso es lo que evita que cualquiera
// se autoasigne ADMIN o se cuelgue de una empresa ajena (ver AuthService.register).
public record RegisterRequest(

        @NotBlank(message = "El nombre de la empresa es obligatorio")
        @Size(max = 150)
        String companyName,

        @NotBlank(message = "El email de la empresa es obligatorio")
        @Email(message = "El email de la empresa no es válido")
        @Size(max = 150)
        String companyEmail,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String name,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido")
        @Size(max = 150)
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener al menos 8 caracteres")
        String password
) {
}
