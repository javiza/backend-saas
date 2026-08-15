package com.businessplatform.backend.companyapplication.service;

import com.businessplatform.backend.application.entity.Application;
import com.businessplatform.backend.application.repository.ApplicationRepository;
import com.businessplatform.backend.company.entity.Company;
import com.businessplatform.backend.company.repository.CompanyRepository;
import com.businessplatform.backend.company.service.CompanyNotFoundException;
import com.businessplatform.backend.companyapplication.dto.AccessCheckResponse;
import com.businessplatform.backend.companyapplication.dto.AssignApplicationRequest;
import com.businessplatform.backend.companyapplication.dto.CompanyApplicationResponse;
import com.businessplatform.backend.companyapplication.entity.CompanyApplication;
import com.businessplatform.backend.companyapplication.repository.CompanyApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CompanyApplicationService {

    private final CompanyApplicationRepository companyApplicationRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;

    public CompanyApplicationService(
            CompanyApplicationRepository companyApplicationRepository,
            CompanyRepository companyRepository,
            ApplicationRepository applicationRepository
    ) {
        this.companyApplicationRepository = companyApplicationRepository;
        this.companyRepository = companyRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public CompanyApplicationResponse assign(
            UUID companyId,
            AssignApplicationRequest request
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new CompanyNotFoundException("Empresa no encontrada: " + companyId));

        Application application = applicationRepository.findById(request.applicationId())
                .orElseThrow(() ->
                        new RuntimeException("Aplicación no encontrada: " + request.applicationId()));

        if (companyApplicationRepository.existsByCompanyIdAndApplicationId(
                companyId, request.applicationId())) {
            throw new CompanyApplicationAlreadyAssignedException(
                    "La empresa \"" + company.getName() + "\" ya tiene contratada \""
                            + application.getName() + "\""
            );
        }

        CompanyApplication companyApplication = new CompanyApplication(company, application);
        companyApplication.setExpiresAt(request.expiresAt());

        return toResponse(companyApplicationRepository.save(companyApplication));
    }

    @Transactional(readOnly = true)
    public List<CompanyApplicationResponse> findByCompany(UUID companyId) {
        return companyApplicationRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CompanyApplicationResponse setActive(
            UUID companyId,
            UUID applicationId,
            boolean active
    ) {
        CompanyApplication companyApplication = companyApplicationRepository
                .findByCompanyIdAndApplicationId(companyId, applicationId)
                .orElseThrow(() ->
                        new CompanyApplicationNotFoundException(
                                "La empresa no tiene contratada esa aplicación"
                        ));

        companyApplication.setActive(active);

        return toResponse(companyApplication);
    }

    /**
     * Punto de entrada que agencia_turismo y control_acceso deben llamar
     * (o replicar validando el JWT + esta regla) para saber si dejan
     * entrar a un usuario de una empresa determinada.
     */
    @Transactional(readOnly = true)
    public AccessCheckResponse checkAccess(UUID companyId, String applicationCode) {

        var subscription = companyApplicationRepository
                .findByCompanyIdAndApplication_Code(companyId, applicationCode);

        if (subscription.isEmpty()) {
            return AccessCheckResponse.denied("La empresa no tiene contratada esta aplicación");
        }

        CompanyApplication companyApplication = subscription.get();

        if (!companyApplication.isActive()) {
            return AccessCheckResponse.denied("La suscripción está desactivada");
        }

        if (companyApplication.getExpiresAt() != null
                && companyApplication.getExpiresAt().isBefore(LocalDateTime.now())) {
            return AccessCheckResponse.denied("La suscripción venció");
        }

        return AccessCheckResponse.granted();
    }

    private CompanyApplicationResponse toResponse(CompanyApplication companyApplication) {
        return new CompanyApplicationResponse(
                companyApplication.getId(),
                companyApplication.getCompany().getId(),
                companyApplication.getCompany().getName(),
                companyApplication.getApplication().getId(),
                companyApplication.getApplication().getCode(),
                companyApplication.getApplication().getName(),
                companyApplication.isActive(),
                companyApplication.getExpiresAt(),
                companyApplication.getCreatedAt()
        );
    }
}
