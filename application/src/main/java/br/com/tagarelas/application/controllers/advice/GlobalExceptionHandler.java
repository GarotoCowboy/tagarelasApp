package br.com.tagarelas.application.controllers.advice;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainException;
import br.com.tagarelas.core.exceptions.DomainValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<Map<String, String>> domainValidationException(DomainValidationException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<Map<String, String>> businessRuleViolationException(BusinessRuleViolationException ex) {

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> domainException(DomainException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex.getErrorCode() != null) {
            String codeName = ex.getErrorCode().name();

            if (codeName.contains("NOT_FOUND")) {
                status = HttpStatus.NOT_FOUND;
            }
            if (codeName.contains("UNAUTHORIZED")) {
                status = HttpStatus.FORBIDDEN;
            }
            if (codeName.contains("INVALID_CREDENTIALS")) {
                status = HttpStatus.UNAUTHORIZED;
            }
            return ResponseEntity.status(status).body(Map.of("code",ex.getErrorCode().getCode(),"error",ex.getMessage()));
        }

        return ResponseEntity.status(status).body(Map.of("error", ex.getMessage()));
    }

}
