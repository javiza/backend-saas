package com.businessplatform.backend.plan.service;

public class PlanAlreadyExistsException extends RuntimeException {
    public PlanAlreadyExistsException(String message) {
        super(message);
    }
}
