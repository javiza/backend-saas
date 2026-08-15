package com.businessplatform.backend.payment.client;

import com.businessplatform.backend.payment.client.dto.ChargeResult;
import com.businessplatform.backend.payment.client.dto.InscriptionFinishResult;
import com.businessplatform.backend.payment.client.dto.InscriptionStartResult;
import com.businessplatform.backend.payment.config.TransbankProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Habla directamente con la API REST de Oneclick (Mall) de Transbank —
 * https://www.transbankdevelopers.cl/referencia/oneclick — en vez de
 * depender del SDK oficial, para no atarnos a una versión específica de
 * su librería Java. Aunque el SaaS solo tiene un comercio, Oneclick
 * siempre opera en modalidad "Mall": el cobro se hace contra un
 * childCommerceCode (ver TransbankProperties).
 */
@Component
public class TransbankOneclickClient {

    private static final String INSCRIPTIONS_PATH = "/rswebpaytransaction/api/oneclick/v1.2/inscriptions";
    private static final String TRANSACTIONS_PATH = "/rswebpaytransaction/api/oneclick/v1.2/transactions";

    private final RestClient restClient;
    private final TransbankProperties properties;

    public TransbankOneclickClient(TransbankProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("Tbk-Api-Key-Id", properties.getCommerceCode())
                .defaultHeader("Tbk-Api-Key-Secret", properties.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Arranca la inscripción de una tarjeta. El frontend debe tomar
     * urlWebpay + token y hacer un POST (auto-submit de un <form>) con
     * TBK_TOKEN=token a esa URL para que el usuario ingrese su tarjeta.
     */
    public InscriptionStartResult startInscription(String username, String email) {
        return restClient.post()
                .uri(INSCRIPTIONS_PATH)
                .body(Map.of(
                        "username", username,
                        "email", email,
                        "response_url", properties.getResponseUrl()
                ))
                .retrieve()
                .body(InscriptionStartResult.class);
    }

    /**
     * Confirma la inscripción con el token que Transbank devuelve (como
     * token_ws) al response_url tras cargar los datos de la tarjeta.
     * IMPORTANTE: el body debe ir vacío, Transbank responde 422 si no.
     */
    public InscriptionFinishResult finishInscription(String token) {
        return restClient.put()
                .uri(INSCRIPTIONS_PATH + "/{token}", token)
                .body(Map.of())
                .retrieve()
                .body(InscriptionFinishResult.class);
    }

    /** Elimina la tarjeta inscrita (el usuario deja de tener medio de pago). */
    public void deleteInscription(String tbkUser, String username) {
        restClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri(INSCRIPTIONS_PATH)
                .body(Map.of("tbk_user", tbkUser, "username", username))
                .retrieve()
                .toBodilessEntity();
    }

    /** Cobra el monto indicado a la tarjeta inscrita (tbkUser). */
    public ChargeResult charge(String username, String tbkUser, String buyOrder, BigDecimal amount) {
        Map<String, Object> detail = Map.of(
                "commerce_code", properties.getChildCommerceCode(),
                "buy_order", buyOrder,
                "amount", amount.longValue(),
                "installments_number", 1
        );

        return restClient.post()
                .uri(TRANSACTIONS_PATH)
                .body(Map.of(
                        "username", username,
                        "tbk_user", tbkUser,
                        "buy_order", buyOrder,
                        "details", List.of(detail)
                ))
                .retrieve()
                .body(ChargeResult.class);
    }
}
