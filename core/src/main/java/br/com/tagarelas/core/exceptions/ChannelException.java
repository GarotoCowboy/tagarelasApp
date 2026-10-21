package br.com.tagarelas.core.exceptions;

public class ChannelException extends DomainException {

    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

