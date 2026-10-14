package br.com.tagarelas.core.enums;

public enum UserStatus {
PENDING_VERIFICATION(1),
ACTIVE(2),
BANNED(3),
DELETED(4);

private final int code;

UserStatus(int code){
    this.code = code;
}

public int getCode() {
    return code;
}
}
