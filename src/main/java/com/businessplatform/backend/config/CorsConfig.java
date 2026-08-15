package com.businessplatform.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SIN ESTA CLASE, EL FRONTEND NO PUEDE HABLAR CON EL BACKEND.
 *
 * El backend no tenía ninguna configuración de CORS. Spring Security,
 * por defecto, bloquea cualquier request que venga de un origen (dominio/puerto)
 * distinto al del backend. Como el frontend siempre vive en otro origen
 * (otro puerto en local, u otro subdominio en producción), todas las
 * llamadas desde el navegador fallaban silenciosamente con error de CORS
 * en la consola, aunque el backend estuviera perfectamente funcional.
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(allowedOrigins.split(","))
        );
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
