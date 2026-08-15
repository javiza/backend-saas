package com.businessplatform.backend.payment.service;

/** La empresa intenta contratar/renovar un plan pago sin tarjeta registrada. */
public class NoPaymentMethodException extends RuntimeException {
    public NoPaymentMethodException(String message) {
        super(message);
    }
}
