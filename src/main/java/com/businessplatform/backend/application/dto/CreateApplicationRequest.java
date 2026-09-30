package com.businessplatform.backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateApplicationRequest {

    @NotBlank(message = "El código es obligatorio")
    @Pattern(
            regexp = "^[A-Z][A-Z0-9_]*$",
            message = "El código debe ir en mayúsculas, sin espacios (ej: CONTROL_ACCESS)"
    )
    @Size(max = 50)
    private String code;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120)
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500)
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
