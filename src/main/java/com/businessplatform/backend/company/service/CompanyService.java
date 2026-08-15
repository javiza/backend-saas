package com.businessplatform.backend.company.service;

import com.businessplatform.backend.company.dto.CompanyResponse;
import com.businessplatform.backend.company.dto.CreateCompanyRequest;
import com.businessplatform.backend.company.entity.Company;
import com.businessplatform.backend.company.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CompanyResponse create(CreateCompanyRequest request) {

        if (companyRepository.existsByEmail(request.email())) {
            throw new CompanyAlreadyExistsException(
                    "Ya existe una empresa con el email: " + request.email()
            );
        }

        Company company = new Company();
        company.setName(request.name());
        company.setEmail(request.email());

        Company savedCompany = companyRepository.save(company);

        return toResponse(savedCompany);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> findAll() {
        return companyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyResponse findById(UUID id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() ->
                        new CompanyNotFoundException(
                                "Empresa no encontrada: " + id
                        )
                );

        return toResponse(company);
    }

    private CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getEmail(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}
