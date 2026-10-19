package br.com.tagarelas.core.exceptions;

public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }

    public BusinessRuleViolationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessRuleViolationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

