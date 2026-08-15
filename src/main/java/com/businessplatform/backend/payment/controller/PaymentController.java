package com.businessplatform.backend.payment.controller;

import com.businessplatform.backend.common.security.CurrentUser;
import com.businessplatform.backend.payment.dto.PaymentResponse;
import com.businessplatform.backend.payment.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/payments")
public class PaymentController {

    private final PaymentService service;
    private final CurrentUser currentUser;

    public PaymentController(PaymentService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<PaymentResponse> findByCompany(@PathVariable UUID companyId) {
        currentUser.requireAdminOrOwnCompany(companyId);
        return service.findByCompany(companyId);
    }
}
