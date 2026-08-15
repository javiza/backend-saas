package com.businessplatform.backend.payment.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Respuesta de POST /oneclick/v1.2/transactions (cobro a una tarjeta
 * inscrita). "details" trae un resultado por cada commerce_code cobrado;
 * como este SaaS opera con un solo comercio hijo, siempre viene 1 elemento.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChargeResult(
        @JsonProperty("buy_order") String buyOrder,
        @JsonProperty("transaction_date") String transactionDate,
        @JsonProperty("details") List<ChargeDetailResult> details
) {
    public ChargeDetailResult firstDetail() {
        return details == null || details.isEmpty() ? null : details.get(0);
    }

    public boolean approved() {
        ChargeDetailResult detail = firstDetail();
        return detail != null && detail.approved();
    }
}
