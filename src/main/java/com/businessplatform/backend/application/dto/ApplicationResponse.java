package com.businessplatform.backend.application.dto;

import com.businessplatform.backend.application.entity.Application;

import java.time.LocalDateTime;
import java.util.UUID;

public class ApplicationResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;

    public ApplicationResponse(Application application) {
        this.id = application.getId();
        this.code = application.getCode();
        this.name = application.getName();
        this.description = application.getDescription();
        this.active = application.isActive();
        this.createdAt = application.getCreatedAt();
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
