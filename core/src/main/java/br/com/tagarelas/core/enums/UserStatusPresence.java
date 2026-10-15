package br.com.tagarelas.core.enums;

import br.com.tagarelas.core.exceptions.DomainValidationException;

public enum UserStatusPresence {
    ONLINE(1, "Online"),
    IDLE(2, "Idle"),
    DO_NOT_DISTURB(3, "Do not disturb"),
    OFFLINE(4, "Offline");

    private final int code;
    private final String description;

    UserStatusPresence(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UserStatusPresence fromCode(int code) {
        for (UserStatusPresence presence : values()) {
            if (presence.getCode() == code) {
                return presence;
            }
        }
        throw new DomainValidationException("Invalid UserStatusPresence code: " + code);
    }
}
