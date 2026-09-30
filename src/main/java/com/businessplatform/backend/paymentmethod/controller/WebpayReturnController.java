package com.businessplatform.backend.paymentmethod.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Recibe el POST que Transbank hace al terminar (o cancelar) la
 * inscripción de una tarjeta Oneclick (ver transbank.oneclick.response-url).
 * Un navegador no expone el body de una navegación POST a JavaScript, así
 * que este endpoint existe solo para convertir ese POST en un redirect
 * GET al portal cliente con el token como query param — eso sí lo puede
 * leer el SPA (ver PaymentCallback.jsx) para llamar a
 * POST /api/companies/{companyId}/payment-methods/finish.
 *
 * Público (no requiere JWT): quien nos llama es el navegador del usuario
 * redirigido desde Transbank, no una llamada autenticada de nuestro
 * frontend. No expone ni persiste nada por sí mismo.
 */
@RestController
public class WebpayReturnController {

    private final String frontendUrl;

    public WebpayReturnController(@Value("${app.frontend-url}") String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    @PostMapping("/api/payment-methods/webpay-return")
    public ResponseEntity<Void> handleReturn(
            @RequestParam(name = "token_ws", required = false) String tokenWs,
            @RequestParam(name = "TBK_TOKEN", required = false) String tbkTokenCanceled
    ) {
        String redirectUrl;
        if (tokenWs != null && !tokenWs.isBlank()) {
            redirectUrl = frontendUrl + "/payment-methods/callback?token_ws=" + tokenWs;
        } else {
            // El usuario canceló la inscripción en Transbank (o algo
            // falló antes de emitir token_ws): mandamos igual al callback,
            // sin token, para que la pantalla muestre el error.
            redirectUrl = frontendUrl + "/payment-methods/callback?canceled=true";
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(redirectUrl).toString())
                .build();
    }
}
