package com.businessplatform.backend.companysubscription.service;

public class CompanySubscriptionNotFoundException extends RuntimeException {
    public CompanySubscriptionNotFoundException(String message) {
        super(message);
    }
}
