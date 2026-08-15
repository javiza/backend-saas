package com.businessplatform.backend.paymentmethod.repository;

import com.businessplatform.backend.paymentmethod.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    Optional<PaymentMethod> findByCompanyId(UUID companyId);

    boolean existsByCompanyId(UUID companyId);

    void deleteByCompanyId(UUID companyId);
}
