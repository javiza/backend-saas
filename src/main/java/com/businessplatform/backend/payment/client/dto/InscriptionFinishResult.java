package com.businessplatform.backend.payment.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta de PUT /oneclick/v1.2/inscriptions/{token}: confirma si la
 * tarjeta quedó inscrita. responseCode == 0 significa éxito; cualquier
 * otro valor es rechazo (tarjeta inválida, usuario canceló, etc).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InscriptionFinishResult(
        @JsonProperty("response_code") Integer responseCode,
        @JsonProperty("tbk_user") String tbkUser,
        @JsonProperty("authorization_code") String authorizationCode,
        @JsonProperty("card_type") String cardType,
        @JsonProperty("card_number") String cardNumber
) {
    public boolean approved() {
        return responseCode != null && responseCode == 0;
    }
}
