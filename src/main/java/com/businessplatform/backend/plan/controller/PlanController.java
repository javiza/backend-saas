package com.businessplatform.backend.plan.controller;

import com.businessplatform.backend.plan.dto.CreatePlanRequest;
import com.businessplatform.backend.plan.dto.PlanResponse;
import com.businessplatform.backend.plan.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// Catálogo de planes/paquetes: lo que el admin de plataforma "inventa"
// para vender (nombre, precio, ciclo de facturación, días de prueba y
// qué aplicaciones incluye el paquete).
@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService service;

    public PlanController(PlanService service) {
        this.service = service;
    }

    @GetMapping
    public List<PlanResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PlanResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PlanResponse create(@Valid @RequestBody CreatePlanRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PlanResponse update(@PathVariable UUID id, @Valid @RequestBody CreatePlanRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public PlanResponse setActive(@PathVariable UUID id, @RequestParam boolean active) {
        return service.setActive(id, active);
    }
}
