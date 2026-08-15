package com.businessplatform.backend.payment.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta de POST /oneclick/v1.2/inscriptions: token que el frontend
 * debe enviar por POST (como TBK_TOKEN) al formulario que apunta a
 * urlWebpay, para que el usuario ingrese los datos de su tarjeta.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InscriptionStartResult(
        @JsonProperty("token") String token,
        @JsonProperty("url_webpay") String urlWebpay
) {
}
