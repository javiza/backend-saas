package com.businessplatform.backend.payment.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChargeDetailResult(
        @JsonProperty("amount") Double amount,
        @JsonProperty("status") String status,
        @JsonProperty("authorization_code") String authorizationCode,
        @JsonProperty("response_code") Integer responseCode,
        @JsonProperty("commerce_code") String commerceCode,
        @JsonProperty("buy_order") String buyOrder
) {
    public boolean approved() {
        return responseCode != null && responseCode == 0;
    }
}
