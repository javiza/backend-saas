package com.businessplatform.backend.payment.service;

/** Transbank rechazó el cobro (fondos insuficientes, tarjeta bloqueada, etc). */
public class PaymentDeclinedException extends RuntimeException {
    public PaymentDeclinedException(String message) {
        super(message);
    }
}
