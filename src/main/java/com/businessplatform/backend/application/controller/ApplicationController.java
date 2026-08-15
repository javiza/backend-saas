package com.businessplatform.backend.application.controller;

import com.businessplatform.backend.application.dto.ApplicationResponse;
import com.businessplatform.backend.application.dto.CreateApplicationRequest;
import com.businessplatform.backend.application.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<ApplicationResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{code}")
    public ApplicationResponse findByCode(
            @PathVariable String code
    ) {
        return service.findByCode(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApplicationResponse create(
            @Valid @RequestBody CreateApplicationRequest request
    ) {
        return service.create(request);
    }
}
