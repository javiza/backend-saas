package com.businessplatform.backend.companyapplication.repository;

import com.businessplatform.backend.companyapplication.entity.CompanyApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyApplicationRepository
        extends JpaRepository<CompanyApplication, UUID> {

    List<CompanyApplication> findByCompanyId(UUID companyId);

    boolean existsByCompanyIdAndApplicationId(
            UUID companyId,
            UUID applicationId
    );

    Optional<CompanyApplication> findByCompanyIdAndApplicationId(
            UUID companyId,
            UUID applicationId
    );

    // Usado por el endpoint de validación que consultan agencia_turismo
    // y control_acceso antes de dejar entrar a un usuario.
    Optional<CompanyApplication> findByCompanyIdAndApplication_Code(
            UUID companyId,
            String applicationCode
    );
}
