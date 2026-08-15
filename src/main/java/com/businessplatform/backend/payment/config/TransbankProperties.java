package com.businessplatform.backend.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Credenciales y configuración de Oneclick (Transbank) para tarjeta
 * guardada + cobro recurrente. Ver application.yaml -> transbank.oneclick.*
 */
@Component
@ConfigurationProperties(prefix = "transbank.oneclick")
public class TransbankProperties {

    private Environment environment = Environment.INTEGRATION;
    private String commerceCode;
    private String apiKey;
    private String childCommerceCode;
    private String responseUrl;

    public enum Environment {
        INTEGRATION,
        PRODUCTION
    }

    public String baseUrl() {
        return environment == Environment.PRODUCTION
                ? "https://webpay3g.transbank.cl"
                : "https://webpay3gint.transbank.cl";
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getCommerceCode() {
        return commerceCode;
    }

    public void setCommerceCode(String commerceCode) {
        this.commerceCode = commerceCode;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getChildCommerceCode() {
        return childCommerceCode;
    }

    public void setChildCommerceCode(String childCommerceCode) {
        this.childCommerceCode = childCommerceCode;
    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }
}
