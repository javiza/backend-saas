package com.businessplatform.backend.user.controller;

import com.businessplatform.backend.user.dto.CreateUserRequest;
import com.businessplatform.backend.user.dto.UserResponse;
import com.businessplatform.backend.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Antes no tenía @PreAuthorize: cualquier usuario autenticado -incluso
    // un MANAGER recién autoregistrado- podía llamar este endpoint pidiendo
    // role: ADMIN y companyId de otra empresa, y quedaba con permisos de
    // administrador de plataforma. Ver BootstrapAdminInitializer para cómo
    // se crea el primer ADMIN ahora que esta puerta está cerrada.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return userService.create(request);
    }
}
