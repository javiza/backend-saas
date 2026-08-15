package com.businessplatform.backend.payment.dto;

import com.businessplatform.backend.payment.entity.Payment;
import com.businessplatform.backend.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID subscriptionId,
        String buyOrder,
        BigDecimal amount,
        PaymentStatus status,
        String authorizationCode,
        LocalDateTime transactionDate,
        LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getCompanySubscription().getId(),
                payment.getBuyOrder(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getAuthorizationCode(),
                payment.getTransactionDate(),
                payment.getCreatedAt()
        );
    }
}
