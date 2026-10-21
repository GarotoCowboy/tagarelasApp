package br.com.tagarelas.core.exceptions;

public enum ErrorCode {

    USER_NOT_FOUND_USERNAME("US_0001", "User does not exist with this username"),
    USER_NOT_FOUND_ID("US_0002", "User does not exist with this id"),
    USER_NOT_FOUND_EMAIL("US_0003", "User does not exist with this email"),
    OTP_EXPIRED_OR_NOT_FOUND("US_0004", "OTP code expired or not found"),
    INVALID_OTP_CODE("US_0005", "Invalid OTP code"),
    USER_INVALID_CURRENT_PASSWORD("US_0006", "Current password does not match"),

    AUTH_INVALID_CREDENTIALS("AUTH_0001", "Invalid username/email or password"),
    AUTH_USER_NOT_ACTIVATED("AUTH_0002", "User account is pending verification"),
    AUTH_USER_BANNED("AUTH_0003", "User account is banned"),
    AUTH_USER_DELETED("AUTH_0004", "User account is deleted"),

    MESSAGE_CONTENT_TOO_LONG("MSG_0001", "Message content cannot exceed 2000 characters"),
    MESSAGE_EMPTY("MSG_0002", "Message must contain text or at least one attachment"),
    MESSAGE_NOT_FOUND("MSG_0003", "Message not found"),
    MESSAGE_UNAUTHORIZED_ACTION("MSG_0004", "Only the message author can perform this action"),

    FRIENDSHIP_NOT_FOUND("FR_0001", "Friendship not found"),
    FRIENDSHIP_ALREADY_EXISTS("FR_0002", "Friendship or request already exists between these users"),
    FRIENDSHIP_BLOCKED("FR_0003", "Cannot send friend request because there is a block between users"),

    GUILD_NOT_FOUND("GD_0001", "Guild not found"),
    GUILD_UNAUTHORIZED_ACTION("GD_0002", "You do not have permission to perform this action in the guild"),
    GUILD_INVITE_NOT_FOUND("GD_0003", "Guild invite not found"),

    GROUP_NOT_FOUND("GRP_0001", "Group not found"),
    GROUP_UNAUTHORIZED_ACTION("GRP_0002", "You do not have permission to perform this action in the group"),

    CHANNEL_NOT_FOUND("CH_0001", "Channel not found"),
    CHANNEL_UNAUTHORIZED_ACTION("CH_0002", "You do not have permission to manage this channel"),
    DM_BLOCKED_BY_FRIENDSHIP("CH_0003", "Cannot open a DM because there is a block between users"),

    ROLE_NOT_FOUND("RL_0001", "Role not found"),
    ROLE_UNAUTHORIZED_ACTION("RL_0002", "You do not have permission to manage roles in this guild");



    private final String code;
    private final String errorDescription;

    ErrorCode(String code,String errorDescription){
        this.code = code;
        this.errorDescription = errorDescription;
    }

    public String getCode(){
        return code;
    }

    public String getErrorDescription(){
        return errorDescription;
    }
}
