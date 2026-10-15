package br.com.tagarelas.core.enums;

import br.com.tagarelas.core.exceptions.DomainValidationException;

public enum FriendShipStatus {
PENDING(1, "Pending"),
ACCEPTED(2, "Accepted"),
DECLINED(3, "Declined"),
BLOCKED(4, "Blocked");


 private final int code;
    private final String description;

    FriendShipStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static FriendShipStatus fromCode(int code) {
        for (FriendShipStatus kind : values()) {
            if (kind.getCode() == code) {
                return kind;
            }
        }
        throw new DomainValidationException("Invalid FriendShipStatus code: " + code);
    }
}
