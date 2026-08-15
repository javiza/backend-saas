package com.businessplatform.backend.companysubscription.repository;

import com.businessplatform.backend.companysubscription.entity.CompanySubscription;
import com.businessplatform.backend.companysubscription.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription, UUID> {

    List<CompanySubscription> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);

    List<CompanySubscription> findByStatusIn(List<SubscriptionStatus> statuses);

    boolean existsByCompanyIdAndPlanIdAndStatusIn(UUID companyId, UUID planId, List<SubscriptionStatus> statuses);

    // Suscripciones vivas (TRIAL o ACTIVE) cuyo período vigente ya venció:
    // las usa el job de expiración para pasarlas a EXPIRED y cortar el acceso.
    @Query("""
            SELECT s FROM CompanySubscription s
            WHERE s.status IN :statuses
            AND s.currentPeriodEnd IS NOT NULL
            AND s.currentPeriodEnd < :now
            """)
    List<CompanySubscription> findExpired(
            @Param("statuses") List<SubscriptionStatus> statuses,
            @Param("now") LocalDateTime now
    );

    // Trials que vencen dentro de las próximas N horas: para que el admin
    // vea "empresas por vencer su prueba gratis" antes de que pase.
    @Query("""
            SELECT s FROM CompanySubscription s
            WHERE s.status = :status
            AND s.trialEndsAt BETWEEN :now AND :limit
            ORDER BY s.trialEndsAt ASC
            """)
    List<CompanySubscription> findTrialsEndingBefore(
            @Param("status") SubscriptionStatus status,
            @Param("now") LocalDateTime now,
            @Param("limit") LocalDateTime limit
    );
}
