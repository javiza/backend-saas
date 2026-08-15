package com.businessplatform.backend.common.exception;

import com.businessplatform.backend.company.service.CompanyAlreadyExistsException;
import com.businessplatform.backend.company.service.CompanyNotFoundException;
import com.businessplatform.backend.companyapplication.service.CompanyApplicationAlreadyAssignedException;
import com.businessplatform.backend.companyapplication.service.CompanyApplicationNotFoundException;
import com.businessplatform.backend.companysubscription.service.CompanySubscriptionConflictException;
import com.businessplatform.backend.companysubscription.service.CompanySubscriptionNotFoundException;
import com.businessplatform.backend.plan.service.PlanAlreadyExistsException;
import com.businessplatform.backend.plan.service.PlanNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Antes no existía: AuthService.login() y UserService.create() lanzan
    // IllegalArgumentException (credenciales inválidas, empresa no existe,
    // usuario duplicado) y, al no tener handler, Spring devolvía 500 en vez
    // de un error claro para el frontend.
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        return new ApiError(400, exception.getMessage(), LocalDateTime.now());
    }

    // Antes no existía: al bloquear el IDOR en CompanyApplicationController
    // (ver CurrentUser.requireAdminOrOwnCompany), sin este handler la
    // AccessDeniedException también caía en un 500 genérico.
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessDenied(
            AccessDeniedException exception
    ) {
        return new ApiError(403, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(CompanyAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCompanyAlreadyExists(
            CompanyAlreadyExistsException exception
    ) {
        return new ApiError(
                409,
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCompanyNotFound(
            CompanyNotFoundException exception
    ) {
        return new ApiError(
                404,
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(CompanyApplicationAlreadyAssignedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCompanyApplicationAlreadyAssigned(
            CompanyApplicationAlreadyAssignedException exception
    ) {
        return new ApiError(409, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(CompanyApplicationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCompanyApplicationNotFound(
            CompanyApplicationNotFoundException exception
    ) {
        return new ApiError(404, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(PlanNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handlePlanNotFound(PlanNotFoundException exception) {
        return new ApiError(404, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(PlanAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlePlanAlreadyExists(PlanAlreadyExistsException exception) {
        return new ApiError(409, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(CompanySubscriptionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCompanySubscriptionNotFound(CompanySubscriptionNotFoundException exception) {
        return new ApiError(404, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(CompanySubscriptionConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCompanySubscriptionConflict(CompanySubscriptionConflictException exception) {
        return new ApiError(409, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Datos inválidos");

        return new ApiError(
                400,
                message,
                LocalDateTime.now()
        );
    }
}
