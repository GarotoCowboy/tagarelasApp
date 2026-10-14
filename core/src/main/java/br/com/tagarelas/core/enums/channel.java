package br.com.tagarelas.core.enums;

public enum channel {

    GUILD_TEXT(1),
    GROUP(2),
    DM(3);

    private final int code;

    channel(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
