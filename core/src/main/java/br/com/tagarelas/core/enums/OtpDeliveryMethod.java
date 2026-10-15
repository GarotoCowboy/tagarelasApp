package br.com.tagarelas.core.enums;

import br.com.tagarelas.core.exceptions.DomainValidationException;

public enum OtpDeliveryMethod {
EMAIL(1,"Email"),
SMS(2,"SMS");


    private final int code;
    private final String description;

    OtpDeliveryMethod(int code, String description) {
        this.code = code;
        this.description = description;
    }

 public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OtpDeliveryMethod fromCode(int code) {
        for (OtpDeliveryMethod presence : values()) {
            if (presence.getCode() == code) {
                return presence;
            }
        }
        throw new DomainValidationException("Invalid OtpDeliveryMethod code: " + code);
    }
}
