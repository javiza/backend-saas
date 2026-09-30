package com.businessplatform.backend.config;

import com.businessplatform.backend.company.entity.Company;
import com.businessplatform.backend.company.repository.CompanyRepository;
import com.businessplatform.backend.user.entity.Role;
import com.businessplatform.backend.user.entity.User;
import com.businessplatform.backend.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Antes no existía ninguna forma de obtener el primer usuario ADMIN de
 * plataforma: /api/auth/register siempre crea una empresa nueva con un
 * usuario MANAGER (ver AuthService.register), y POST /api/users ahora
 * requiere ya estar autenticado como ADMIN (@PreAuthorize en
 * UserController). Sin esto, la plataforma quedaba sin salida: nadie
 * podría haber llegado nunca a ADMIN.
 * <p>
 * Este runner corre en cada arranque pero solo hace algo si TODAVÍA no
 * existe ningún usuario con rol ADMIN. Crea (o reutiliza) una empresa
 * "Plataforma" interna y le cuelga el ADMIN semilla. Es idempotente: en
 * arranques siguientes, existsByRole(ADMIN) ya es true y no hace nada.
 */
@Configuration
public class BootstrapAdminInitializer {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminInitializer.class);
    private static final String PLATFORM_COMPANY_NAME = "Plataforma";

    @Bean
    CommandLineRunner bootstrapPlatformAdmin(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.email}") String adminEmail,
            @Value("${app.bootstrap-admin.password}") String adminPassword
    ) {
        return args -> {

            if (userRepository.existsByRole(Role.ADMIN)) {
                return;
            }

            Company platformCompany = companyRepository.findByEmail(adminEmail)
                    .orElseGet(() -> {
                        Company company = new Company();
                        company.setName(PLATFORM_COMPANY_NAME);
                        company.setEmail(adminEmail);
                        return companyRepository.save(company);
                    });

            User admin = new User();
            admin.setName("Administrador de plataforma");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setCompany(platformCompany);

            userRepository.save(admin);

            log.warn(
                    "==================================================================\n" +
                    "Se creó el ADMIN de plataforma semilla:\n" +
                    "  email:    {}\n" +
                    "  password: (la definida en BOOTSTRAP_ADMIN_PASSWORD / app.bootstrap-admin.password)\n" +
                    "Cambia esta contraseña antes de exponer esto en producción.\n" +
                    "==================================================================",
                    adminEmail
            );
        };
    }
}
