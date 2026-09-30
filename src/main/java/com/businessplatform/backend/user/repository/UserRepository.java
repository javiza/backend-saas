package com.businessplatform.backend.user.repository;

import com.businessplatform.backend.user.entity.Role;
import com.businessplatform.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Usado por BootstrapAdminInitializer para saber si ya existe algún
    // ADMIN de plataforma y así no crear el admin semilla más de una vez.
    boolean existsByRole(Role role);

    boolean existsByEmailAndCompanyId(
            String email,
            UUID companyId
    );

    Optional<User> findByEmailAndCompanyId(
            String email,
            UUID companyId
    );

    Optional<User> findByEmail(String email);
}
