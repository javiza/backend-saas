package com.businessplatform.backend.paymentmethod.service;

import com.businessplatform.backend.company.entity.Company;
import com.businessplatform.backend.company.repository.CompanyRepository;
import com.businessplatform.backend.company.service.CompanyNotFoundException;
import com.businessplatform.backend.payment.client.TransbankOneclickClient;
import com.businessplatform.backend.payment.client.dto.InscriptionFinishResult;
import com.businessplatform.backend.payment.client.dto.InscriptionStartResult;
import com.businessplatform.backend.paymentmethod.dto.PaymentMethodResponse;
import com.businessplatform.backend.paymentmethod.dto.StartInscriptionResponse;
import com.businessplatform.backend.paymentmethod.entity.PaymentMethod;
import com.businessplatform.backend.paymentmethod.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Inscripción y borrado de tarjeta guardada (Oneclick). El cobro en sí
 * (charge) vive en PaymentService, que es lo que usa
 * CompanySubscriptionService al contratar/renovar un plan.
 */
@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final CompanyRepository companyRepository;
    private final TransbankOneclickClient transbankClient;

    public PaymentMethodService(
            PaymentMethodRepository paymentMethodRepository,
            CompanyRepository companyRepository,
            TransbankOneclickClient transbankClient
    ) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.companyRepository = companyRepository;
        this.transbankClient = transbankClient;
    }

    /**
     * Arranca la inscripción. El "username" que le mandamos a Transbank
     * es interno (no el email de login del usuario que está logueado);
     * usamos el id de la empresa para que sea estable y único.
     */
    @Transactional(readOnly = true)
    public StartInscriptionResponse startInscription(UUID companyId, String email) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Empresa no encontrada: " + companyId));

        String username = "company-" + company.getId();

        try {
            InscriptionStartResult result = transbankClient.startInscription(username, email);
            return new StartInscriptionResponse(result.token(), result.urlWebpay());
        } catch (RestClientException e) {
            throw new PaymentMethodInscriptionFailedException(
                    "No se pudo iniciar la inscripción de la tarjeta con Transbank: " + e.getMessage()
            );
        }
    }

    /**
     * Confirma la inscripción con el token que Transbank devolvió al
     * response_url (como token_ws) y guarda (o reemplaza) la tarjeta de
     * la empresa.
     */
    @Transactional
    public PaymentMethodResponse finishInscription(UUID companyId, String token) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Empresa no encontrada: " + companyId));

        InscriptionFinishResult result;
        try {
            result = transbankClient.finishInscription(token);
        } catch (RestClientException e) {
            throw new PaymentMethodInscriptionFailedException(
                    "No se pudo confirmar la inscripción con Transbank: " + e.getMessage()
            );
        }

        if (!result.approved()) {
            throw new PaymentMethodInscriptionFailedException(
                    "Transbank rechazó la inscripción de la tarjeta (código " + result.responseCode() + ")"
            );
        }

        // Si la empresa ya tenía una tarjeta, la reemplazamos por la nueva.
        paymentMethodRepository.findByCompanyId(companyId)
                .ifPresent(paymentMethodRepository::delete);

        String username = "company-" + company.getId();
        String cardLast4 = result.cardNumber() == null || result.cardNumber().length() < 4
                ? result.cardNumber()
                : result.cardNumber().substring(result.cardNumber().length() - 4);

        PaymentMethod paymentMethod = new PaymentMethod(
                company, result.tbkUser(), username, result.cardType(), cardLast4
        );
        paymentMethod = paymentMethodRepository.save(paymentMethod);

        return PaymentMethodResponse.from(paymentMethod);
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponse findByCompany(UUID companyId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new PaymentMethodNotFoundException(
                        "La empresa no tiene una tarjeta registrada"
                ));
        return PaymentMethodResponse.from(paymentMethod);
    }

    @Transactional
    public void remove(UUID companyId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new PaymentMethodNotFoundException(
                        "La empresa no tiene una tarjeta registrada"
                ));

        try {
            transbankClient.deleteInscription(paymentMethod.getTbkUser(), paymentMethod.getUsername());
        } catch (RestClientException e) {
            // Si Transbank ya no tiene la inscripción (por ejemplo, se borró
            // desde su lado) igual limpiamos nuestro registro local.
        }

        paymentMethodRepository.delete(paymentMethod);
    }
}
