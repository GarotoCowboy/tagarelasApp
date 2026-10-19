package br.com.tagarelas.core.exceptions;

public class DomainValidationException extends DomainException {

    public DomainValidationException(String message) {
        super(message);
    }

    public DomainValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DomainValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

