package br.com.tagarelas.core.exceptions;

public class MessageException extends DomainException {

    protected MessageException(ErrorCode errorCode) {
        super(errorCode);

    }

    public MessageException(String message) {
        super(message);
    }


    public MessageException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }



    

}
