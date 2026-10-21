package br.com.tagarelas.services.output;

import java.time.LocalDateTime;
import java.util.UUID;

public record GuildInviteOutput(
    UUID id,
    String code,
    UUID guildId,
    UUID creatorId,
    int maxUses,
    int currentUses,
    LocalDateTime expiresAt,
    LocalDateTime createdAt,
    boolean isDeleted
) {

}

