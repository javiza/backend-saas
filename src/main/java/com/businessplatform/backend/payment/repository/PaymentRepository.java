package com.businessplatform.backend.payment.repository;

import com.businessplatform.backend.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByCompanySubscriptionIdOrderByCreatedAtDesc(UUID companySubscriptionId);

    List<Payment> findByCompanySubscriptionCompanyIdOrderByCreatedAtDesc(UUID companyId);
}
