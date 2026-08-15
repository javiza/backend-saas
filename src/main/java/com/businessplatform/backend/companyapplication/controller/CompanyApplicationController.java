package com.businessplatform.backend.companyapplication.controller;

import com.businessplatform.backend.companyapplication.dto.AccessCheckResponse;
import com.businessplatform.backend.companyapplication.dto.AssignApplicationRequest;
import com.businessplatform.backend.companyapplication.dto.CompanyApplicationResponse;
import com.businessplatform.backend.companyapplication.service.CompanyApplicationService;
import com.businessplatform.backend.common.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/applications")
public class CompanyApplicationController {

    private final CompanyApplicationService service;
    private final CurrentUser currentUser;

    public CompanyApplicationController(CompanyApplicationService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    // Esto es "vender" agencia_turismo o control_acceso a una empresa:
    // se crea el vínculo company <-> application.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyApplicationResponse assign(
            @PathVariable UUID companyId,
            @Valid @RequestBody AssignApplicationRequest request
    ) {
        return service.assign(companyId, request);
    }

    // Antes: cualquier usuario autenticado podía leer las aplicaciones de
    // CUALQUIER empresa con solo cambiar el companyId en la URL (IDOR).
    // Ahora: solo el ADMIN de plataforma o un usuario de esa misma empresa.
    @GetMapping
    public List<CompanyApplicationResponse> findByCompany(
            @PathVariable UUID companyId
    ) {
        currentUser.requireAdminOrOwnCompany(companyId);
        return service.findByCompany(companyId);
    }

    @PatchMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyApplicationResponse setActive(
            @PathVariable UUID companyId,
            @PathVariable UUID applicationId,
            @RequestParam boolean active
    ) {
        return service.setActive(companyId, applicationId, active);
    }

    // Endpoint que agencia_turismo / control_acceso llaman (server-to-server)
    // para confirmar que la empresa del usuario logueado tiene acceso vigente
    // a esa aplicación antes de dejarlo entrar.
    @GetMapping("/{code}/access")
    public AccessCheckResponse checkAccess(
            @PathVariable UUID companyId,
            @PathVariable String code
    ) {
        return service.checkAccess(companyId, code);
    }
}
