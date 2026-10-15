package br.com.tagarelas.core.enums;

import br.com.tagarelas.core.exceptions.DomainValidationException;

public enum MessageType {
    TEXT(1, "Text"),
    FILE(2, "File"),
    GIF(3, "Gif"),
    STICKER(4, "Sticker");

    private final int code;
    private final String description;

    MessageType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MessageType fromCode(int code) {
        for (MessageType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new DomainValidationException("Invalid MessageType code: " + code);
    }
}
