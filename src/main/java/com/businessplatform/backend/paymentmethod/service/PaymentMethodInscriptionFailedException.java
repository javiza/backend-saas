package com.businessplatform.backend.paymentmethod.service;

/** La inscripción de la tarjeta fue rechazada por Transbank (o el usuario la canceló). */
public class PaymentMethodInscriptionFailedException extends RuntimeException {
    public PaymentMethodInscriptionFailedException(String message) {
        super(message);
    }
}
