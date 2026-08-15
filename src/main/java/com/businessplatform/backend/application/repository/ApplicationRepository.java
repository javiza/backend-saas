package com.businessplatform.backend.application.repository;

import com.businessplatform.backend.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository
        extends JpaRepository<Application, UUID> {

    Optional<Application> findByCode(String code);

    boolean existsByCode(String code);
}
