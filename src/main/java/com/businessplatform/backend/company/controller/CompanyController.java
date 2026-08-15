package com.businessplatform.backend.company.controller;

import com.businessplatform.backend.company.dto.CompanyResponse;
import com.businessplatform.backend.company.dto.CreateCompanyRequest;
import com.businessplatform.backend.company.service.CompanyService;
import com.businessplatform.backend.common.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final CurrentUser currentUser;

    public CompanyController(CompanyService companyService, CurrentUser currentUser) {
        this.companyService = companyService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse create(
            @Valid @RequestBody CreateCompanyRequest request
    ) {
        return companyService.create(request);
    }

    // Listado completo de empresas: solo tiene sentido para el admin de
    // plataforma (es su cartera de clientes). El frontend del portal
    // cliente no llama a este endpoint.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CompanyResponse> findAll() {
        return companyService.findAll();
    }

    @GetMapping("/{id}")
    public CompanyResponse findById(@PathVariable UUID id) {
        currentUser.requireAdminOrOwnCompany(id);
        return companyService.findById(id);
    }
}
