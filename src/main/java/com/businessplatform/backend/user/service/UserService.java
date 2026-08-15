package com.businessplatform.backend.user.service;

import com.businessplatform.backend.company.entity.Company;
import com.businessplatform.backend.company.repository.CompanyRepository;
import com.businessplatform.backend.user.dto.CreateUserRequest;
import com.businessplatform.backend.user.dto.UserResponse;
import com.businessplatform.backend.user.entity.User;
import com.businessplatform.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {

        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Empresa no encontrada"
                        )
                );

        if (userRepository.existsByEmailAndCompanyId(
                request.email(),
                request.companyId()
        )) {
            throw new IllegalArgumentException(
                    "El usuario ya existe en esta empresa"
            );
        }

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());

        // IMPORTANTE:
        // Nunca guardamos la contraseña en texto plano.
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setRole(request.role());
        user.setCompany(company);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                company.getId(),
                savedUser.isActive(),
                savedUser.getCreatedAt()
        );
    }
}
