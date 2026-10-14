package br.com.tagarelas.core.enums;

public enum UserStatusPresence {

    ONLINE(1),
    IDLE(2),
    DO_NOT_DISTURB(3),
    OFFLINE(4);

    private final int code;

    UserStatusPresence(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
