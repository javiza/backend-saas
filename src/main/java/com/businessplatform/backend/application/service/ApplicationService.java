package com.businessplatform.backend.application.service;

import com.businessplatform.backend.application.dto.ApplicationResponse;
import com.businessplatform.backend.application.dto.CreateApplicationRequest;
import com.businessplatform.backend.application.entity.Application;
import com.businessplatform.backend.application.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository repository;

    public ApplicationService(ApplicationRepository repository) {
        this.repository = repository;
    }

    public List<ApplicationResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(ApplicationResponse::new)
                .toList();
    }

    public ApplicationResponse findByCode(String code) {
        Application application = repository.findByCode(code)
                .orElseThrow(() ->
                        new RuntimeException("Application not found")
                );

        return new ApplicationResponse(application);
    }

    public ApplicationResponse create(CreateApplicationRequest request) {

        if (repository.existsByCode(request.getCode())) {
            throw new RuntimeException(
                    "Application code already exists"
            );
        }

        Application application = new Application(
                request.getCode(),
                request.getName(),
                request.getDescription()
        );

        return new ApplicationResponse(
                repository.save(application)
        );
    }
}
