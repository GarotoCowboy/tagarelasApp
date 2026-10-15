package br.com.tagarelas.core.enums;

import br.com.tagarelas.core.exceptions.DomainValidationException;

public enum UserStatus {
    PENDING_VERIFICATION(1, "Pending Verification"),
    ACTIVE(2, "Active"),
    BANNED(3, "Banned"),
    DELETED(4, "Deleted");

    private final int code;
    private final String description;

    UserStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UserStatus fromCode(int code) {
        for (UserStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new DomainValidationException("Invalid UserStatus code: " + code);
    }
}
