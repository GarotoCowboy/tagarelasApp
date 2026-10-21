package br.com.tagarelas.core.exceptions;

public class AuthException extends DomainException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

