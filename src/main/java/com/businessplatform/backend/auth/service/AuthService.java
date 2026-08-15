package com.businessplatform.backend.auth.service;

import com.businessplatform.backend.auth.dto.LoginRequest;
import com.businessplatform.backend.auth.dto.LoginResponse;
import com.businessplatform.backend.auth.dto.RegisterRequest;
import com.businessplatform.backend.company.entity.Company;
import com.businessplatform.backend.company.repository.CompanyRepository;
import com.businessplatform.backend.company.service.CompanyAlreadyExistsException;
import com.businessplatform.backend.user.entity.Role;
import com.businessplatform.backend.user.entity.User;
import com.businessplatform.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Alta pública de un cliente: crea su empresa y, dentro de ella, el
    // usuario MANAGER que la administra. Devuelve el mismo LoginResponse
    // que /login para dejar al cliente autenticado de inmediato tras
    // registrarse, sin un segundo viaje al backend.
    @Transactional
    public LoginResponse register(RegisterRequest request) {

        if (companyRepository.existsByEmail(request.companyEmail())) {
            throw new CompanyAlreadyExistsException(
                    "Ya existe una empresa con el email: " + request.companyEmail()
            );
        }

        Company company = new Company();
        company.setName(request.companyName());
        company.setEmail(request.companyEmail());
        Company savedCompany = companyRepository.save(company);

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.MANAGER);
        user.setCompany(savedCompany);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return new LoginResponse(
                token,
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedCompany.getId()
        );
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Email o contraseña incorrectos"
                        )
                );

        if (!user.isActive()) {
            throw new IllegalArgumentException(
                    "El usuario está desactivado"
            );
        }

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Email o contraseña incorrectos"
            );
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCompany().getId()
        );
    }
}
