package com.businessplatform.backend.payment.entity;

import com.businessplatform.backend.companysubscription.entity.CompanySubscription;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de un cobro (Oneclick authorize) contra la tarjeta guardada
 * de una empresa: el cobro inicial al contratar un plan sin trial, o
 * cada cobro de renovación disparado por el cron de vencimientos.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_subscription_id", nullable = false)
    private CompanySubscription companySubscription;

    // Identificador único que le mandamos a Transbank en cada cobro.
    @Column(name = "buy_order", nullable = false, unique = true, length = 64)
    private String buyOrder;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "authorization_code", length = 20)
    private String authorizationCode;

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(
            CompanySubscription companySubscription,
            String buyOrder,
            BigDecimal amount,
            PaymentStatus status,
            String authorizationCode,
            Integer responseCode
    ) {
        this.companySubscription = companySubscription;
        this.buyOrder = buyOrder;
        this.amount = amount;
        this.status = status;
        this.authorizationCode = authorizationCode;
        this.responseCode = responseCode;
        this.transactionDate = LocalDateTime.now();
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

    public CompanySubscription getCompanySubscription() {
        return companySubscription;
    }

    public String getBuyOrder() {
        return buyOrder;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
