package com.businessplatform.backend.companysubscription.controller;

import com.businessplatform.backend.common.security.CurrentUser;
import com.businessplatform.backend.companysubscription.dto.CompanySubscriptionResponse;
import com.businessplatform.backend.companysubscription.dto.SubscribeToPlanRequest;
import com.businessplatform.backend.companysubscription.entity.SubscriptionStatus;
import com.businessplatform.backend.companysubscription.service.CompanySubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class CompanySubscriptionController {

    private final CompanySubscriptionService service;
    private final CurrentUser currentUser;

    public CompanySubscriptionController(CompanySubscriptionService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    // Contratar un plan para una empresa: arranca en trial (si el plan
    // tiene días de prueba) o directamente activo, y da de alta el acceso
    // a todas las apps incluidas en el paquete.
    @PostMapping("/api/companies/{companyId}/subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CompanySubscriptionResponse subscribe(
            @PathVariable UUID companyId,
            @Valid @RequestBody SubscribeToPlanRequest request
    ) {
        return service.subscribe(companyId, request.planId());
    }

    @GetMapping("/api/companies/{companyId}/subscriptions")
    public List<CompanySubscriptionResponse> findByCompany(@PathVariable UUID companyId) {
        currentUser.requireAdminOrOwnCompany(companyId);
        return service.findByCompany(companyId);
    }

    @PatchMapping("/api/companies/{companyId}/subscriptions/{subscriptionId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public CompanySubscriptionResponse cancel(
            @PathVariable UUID companyId,
            @PathVariable UUID subscriptionId
    ) {
        return service.cancel(companyId, subscriptionId);
    }

    // Vista global para el admin de plataforma: todas las suscripciones
    // (opcionalmente filtradas por estado) y las que están por vencer su
    // prueba gratis en las próximas 48hs.
    @GetMapping("/api/subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CompanySubscriptionResponse> findAll(
            @RequestParam(required = false) SubscriptionStatus status
    ) {
        return service.findAll(status);
    }

    @GetMapping("/api/subscriptions/trials-ending-soon")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CompanySubscriptionResponse> trialsEndingSoon() {
        return service.findTrialsEndingSoon();
    }
}
