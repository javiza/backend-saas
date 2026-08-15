package com.businessplatform.backend.paymentmethod.controller;

import com.businessplatform.backend.common.security.CurrentUser;
import com.businessplatform.backend.paymentmethod.dto.FinishInscriptionRequest;
import com.businessplatform.backend.paymentmethod.dto.PaymentMethodResponse;
import com.businessplatform.backend.paymentmethod.dto.StartInscriptionRequest;
import com.businessplatform.backend.paymentmethod.dto.StartInscriptionResponse;
import com.businessplatform.backend.paymentmethod.service.PaymentMethodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService service;
    private final CurrentUser currentUser;

    public PaymentMethodController(PaymentMethodService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    // Paso 1: arranca la inscripción de la tarjeta en Transbank. El
    // frontend usa la respuesta para redirigir al usuario a Webpay.
    @PostMapping("/start")
    public StartInscriptionResponse start(
            @PathVariable UUID companyId,
            @Valid @RequestBody StartInscriptionRequest request
    ) {
        currentUser.requireAdminOrOwnCompany(companyId);
        return service.startInscription(companyId, request.email());
    }

    // Paso 2: el frontend llama esto con el token_ws que Transbank le
    // devolvió por POST al response_url, para confirmar y guardar la tarjeta.
    @PostMapping("/finish")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodResponse finish(
            @PathVariable UUID companyId,
            @Valid @RequestBody FinishInscriptionRequest request
    ) {
        currentUser.requireAdminOrOwnCompany(companyId);
        return service.finishInscription(companyId, request.token());
    }

    @GetMapping
    public PaymentMethodResponse get(@PathVariable UUID companyId) {
        currentUser.requireAdminOrOwnCompany(companyId);
        return service.findByCompany(companyId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable UUID companyId) {
        currentUser.requireAdminOrOwnCompany(companyId);
        service.remove(companyId);
    }
}
