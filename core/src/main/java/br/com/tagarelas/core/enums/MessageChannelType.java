package br.com.tagarelas.core.enums;

import br.com.tagarelas.core.exceptions.DomainValidationException;

public enum MessageChannelType {
    GUILD(1, "Guild"),
    GROUP(2, "Group"),
    DM(3, "Dm");

    private final int code;
    private final String description;

    MessageChannelType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MessageChannelType fromCode(int code) {
        for (MessageChannelType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new DomainValidationException("Invalid MessageChannelType code: " + code);
    }
}
