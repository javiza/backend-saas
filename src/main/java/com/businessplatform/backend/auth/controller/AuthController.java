package com.businessplatform.backend.auth.controller;

import com.businessplatform.backend.auth.dto.LoginRequest;
import com.businessplatform.backend.auth.dto.LoginResponse;
import com.businessplatform.backend.auth.dto.RegisterRequest;
import com.businessplatform.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    // Registro público: cualquier persona puede crear su empresa y
    // quedar como su usuario MANAGER (ver SecurityConfig, permitAll).
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }
}
