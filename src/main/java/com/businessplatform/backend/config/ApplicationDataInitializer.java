package com.businessplatform.backend.config;

import com.businessplatform.backend.application.entity.Application;
import com.businessplatform.backend.application.repository.ApplicationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationDataInitializer {

    @Bean
    CommandLineRunner initializeApplications(
            ApplicationRepository repository
    ) {
        return args -> {

            if (!repository.existsByCode("CONTROL_ACCESS")) {

                repository.save(
                        new Application(
                                "CONTROL_ACCESS",
                                "Control de Acceso",
                                "Gestión de ingresos, salidas, visitantes y autorizaciones."
                        )
                );
            }

            if (!repository.existsByCode("TURISMO")) {

                repository.save(
                        new Application(
                                "TURISMO",
                                "Agencia de Turismo",
                                "Gestión de contenido, clientes y operaciones de una agencia de turismo."
                        )
                );
            }
        };
    }
}
