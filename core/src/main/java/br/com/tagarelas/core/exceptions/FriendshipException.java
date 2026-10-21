package br.com.tagarelas.core.exceptions;

public class FriendshipException extends DomainException {

    public FriendshipException(String message) {
        super(message);
    }

    public FriendshipException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FriendshipException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

