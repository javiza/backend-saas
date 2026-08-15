package com.businessplatform.backend.user.dto;

import com.businessplatform.backend.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateUserRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String name,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido")
        @Size(max = 150)
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100)
        String password,

        @NotNull(message = "El rol es obligatorio")
        Role role,

        @NotNull(message = "La empresa es obligatoria")
        UUID companyId
) {
}
