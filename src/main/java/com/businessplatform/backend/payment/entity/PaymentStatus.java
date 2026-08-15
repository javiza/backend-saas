package com.businessplatform.backend.payment.entity;

public enum PaymentStatus {
    AUTHORIZED,  // cobro aprobado por Transbank
    REJECTED     // cobro rechazado (fondos insuficientes, tarjeta inválida, etc)
}
