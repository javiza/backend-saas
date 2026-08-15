package com.businessplatform.backend.companysubscription.entity;

public enum SubscriptionStatus {
    TRIAL,      // período de prueba gratuita en curso
    ACTIVE,     // suscripción paga vigente
    EXPIRED,    // venció (fin de trial o fin de período) y no se renovó
    CANCELED    // cancelada manualmente por el admin
}
