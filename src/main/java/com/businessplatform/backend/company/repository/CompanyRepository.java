package com.businessplatform.backend.company.repository;

import com.businessplatform.backend.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    boolean existsByEmail(String email);

    Optional<Company> findByEmail(String email);
}
