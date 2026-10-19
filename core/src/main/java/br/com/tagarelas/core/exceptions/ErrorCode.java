package br.com.tagarelas.core.exceptions;

public enum ErrorCode {

       USER_NOT_FOUND_USERNAME("US_0001", "User does not exist with this username"),
       USER_NOT_FOUND_ID("US_0002", "User does not exist with this id"),
       USER_NOT_FOUND_EMAIL("US_0003", "User does not exist with this email"),



       MESSAGE_CONTENT_TOO_LONG("MSG_0001", "Message content cannot exceed 2000 characters"),
        MESSAGE_EMPTY("MSG_0002", "Message must contain text or at least one attachment"),
        MESSAGE_NOT_FOUND("MSG_0003", "Message not found");



    private final String code;
    private final String errorDescription;

    ErrorCode(String code,String errorDescription){
        this.code = code;
        this.errorDescription = errorDescription;
    }

    public String getCode(){
        return code;
    }

    public String getErrorDescription(){
        return errorDescription;
    }
}
