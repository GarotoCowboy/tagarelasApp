package br.com.tagarelas.core.exceptions;

public class RoleException extends DomainException {

    public RoleException(String message) {
        super(message);
    }

    public RoleException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RoleException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

