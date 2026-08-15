package com.businessplatform.backend.paymentmethod.dto;

// El frontend debe hacer un POST (auto-submit de formulario) con
// TBK_TOKEN=token hacia urlWebpay para que el usuario ingrese su tarjeta.
public record StartInscriptionResponse(
        String token,
        String urlWebpay
) {
}
