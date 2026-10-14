package br.com.tagarelas.core.enums;

public enum MessageType {
    TEXT(1),
    FILE(2),
    GIF(3),
    STICKER(4);

private final int code;

MessageType(int code){
    this.code = code;
}

public int getCode() {
    return code;
}
}
