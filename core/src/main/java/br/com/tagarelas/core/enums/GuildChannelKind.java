package br.com.tagarelas.core.enums;

import br.com.tagarelas.core.exceptions.DomainValidationException;

public enum GuildChannelKind {
    TEXT(1, "Text Channel"),
    VOICE(2, "Voice Channel");

    private final int code;
    private final String description;

    GuildChannelKind(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static GuildChannelKind fromCode(int code) {
        for (GuildChannelKind kind : values()) {
            if (kind.getCode() == code) {
                return kind;
            }
        }
        throw new DomainValidationException("Invalid GuildChannelKind code: " + code);
    }
}
