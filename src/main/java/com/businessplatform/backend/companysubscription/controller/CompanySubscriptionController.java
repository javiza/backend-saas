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
    // a todas las apps incluidas en el paquete. Lo puede hacer el admin de
    // plataforma (para cualquier empresa) o un usuario de la propia
    // empresa desde el portal cliente (autocontratación).
    @PostMapping("/api/companies/{companyId}/subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanySubscriptionResponse subscribe(
            @PathVariable UUID companyId,
            @Valid @RequestBody SubscribeToPlanRequest request
    ) {
        currentUser.requireAdminOrOwnCompany(companyId);
        return service.subscribe(companyId, request.planId());
    }

    @GetMapping("/api/companies/{companyId}/subscriptions")
    public List<CompanySubscriptionResponse> findByCompany(@PathVariable UUID companyId) {
        currentUser.requireAdminOrOwnCompany(companyId);
        return service.findByCompany(companyId);
    }

    // Dar de baja: el propio cliente puede cancelar su suscripción desde
    // el portal (o el admin, por cualquier empresa). Al cancelar, no se
    // vuelve a cobrar y el acceso a las apps del plan se revoca (ver
    // CompanySubscriptionService.cancel).
    @PatchMapping("/api/companies/{companyId}/subscriptions/{subscriptionId}/cancel")
    public CompanySubscriptionResponse cancel(
            @PathVariable UUID companyId,
            @PathVariable UUID subscriptionId
    ) {
        currentUser.requireAdminOrOwnCompany(companyId);
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
