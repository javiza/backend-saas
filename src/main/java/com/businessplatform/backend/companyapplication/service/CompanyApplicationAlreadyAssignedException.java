package com.businessplatform.backend.companyapplication.service;

public class CompanyApplicationAlreadyAssignedException extends RuntimeException {
    public CompanyApplicationAlreadyAssignedException(String message) {
        super(message);
    }
}
