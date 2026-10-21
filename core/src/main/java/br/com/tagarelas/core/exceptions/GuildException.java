package br.com.tagarelas.core.exceptions;

public class GuildException extends DomainException {

    public GuildException(String message) {
        super(message);
    }

    public GuildException(ErrorCode errorCode) {
        super(errorCode);
    }

    public GuildException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

