package br.com.tagarelas.core.exceptions;

public class UserException extends DomainException {

    public UserException(String message) {
        super(message);
    }

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
