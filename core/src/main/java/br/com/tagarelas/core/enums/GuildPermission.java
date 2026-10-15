package br.com.tagarelas.core.enums;

import br.com.tagarelas.core.exceptions.DomainValidationException;

public enum GuildPermission {
    ADMINISTRATOR(1, "Administrator"),
    MANAGE_GUILD(2, "Manage Server"),
    MANAGE_CHANNELS(3, "Manage Channels"),
    MANAGE_ROLES(4, "Manage Roles"),
    KICK_MEMBERS(5, "Kick Members"),
    BAN_MEMBERS(6, "Ban Members"),
    CREATE_INVITE(7, "Create Invites"),
    SEND_MESSAGES(8, "Send Messages"),
    ATTACH_FILES(9, "Attach Files"),
    CONNECT_VOICE(10, "Connect to Voice"),
    SPEAK_VOICE(11, "Speak in Voice");

    private final int code;
    private final String description;

    GuildPermission(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static GuildPermission fromCode(int code) {
        for (GuildPermission perm : values()) {
            if (perm.getCode() == code) {
                return perm;
            }
        }
        throw new DomainValidationException("Invalid GuildPermission code: " + code);
    }
}

