package com.businessplatform.backend.paymentmethod.dto;

import com.businessplatform.backend.paymentmethod.entity.PaymentMethod;

import java.time.LocalDateTime;
import java.util.UUID;

// Nunca se expone el tbkUser ni el username interno: solo lo necesario
// para que el frontend muestre "tarjeta terminada en 1234".
public record PaymentMethodResponse(
        UUID id,
        String cardType,
        String cardLast4,
        LocalDateTime createdAt
) {
    public static PaymentMethodResponse from(PaymentMethod paymentMethod) {
        return new PaymentMethodResponse(
                paymentMethod.getId(),
                paymentMethod.getCardType(),
                paymentMethod.getCardLast4(),
                paymentMethod.getCreatedAt()
        );
    }
}
