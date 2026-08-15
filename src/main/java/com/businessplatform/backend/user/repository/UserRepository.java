package com.businessplatform.backend.user.repository;

import com.businessplatform.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

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
