package br.com.tagarelas.core.exceptions;

public class GroupException extends DomainException {

    public GroupException(String message) {
        super(message);
    }

    public GroupException(ErrorCode errorCode) {
        super(errorCode);
    }

    public GroupException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

