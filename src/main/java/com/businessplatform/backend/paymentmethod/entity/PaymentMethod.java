package com.businessplatform.backend.paymentmethod.entity;

import com.businessplatform.backend.company.entity.Company;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tarjeta guardada de una empresa vía Oneclick (Transbank): guarda el
 * tbkUser que identifica la inscripción ante Transbank (nunca el número
 * de tarjeta completo, que Transbank no entrega) para poder cobrar cada
 * renovación sin pedirle la tarjeta de nuevo al cliente.
 */
@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column(name = "tbk_user", nullable = false, length = 64)
    private String tbkUser;

    // Identificador de usuario ante Transbank (no es el username de login
    // de nuestra app; se genera internamente al iniciar la inscripción).
    @Column(nullable = false, length = 64)
    private String username;

    @Column(name = "card_type", length = 30)
    private String cardType;

    @Column(name = "card_last4", length = 4)
    private String cardLast4;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PaymentMethod() {
    }

    public PaymentMethod(Company company, String tbkUser, String username, String cardType, String cardLast4) {
        this.company = company;
        this.tbkUser = tbkUser;
        this.username = username;
        this.cardType = cardType;
        this.cardLast4 = cardLast4;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getTbkUser() {
        return tbkUser;
    }

    public String getUsername() {
        return username;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
