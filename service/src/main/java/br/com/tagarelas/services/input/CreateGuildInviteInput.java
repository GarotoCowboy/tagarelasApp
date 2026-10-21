package br.com.tagarelas.services.input;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateGuildInviteInput(
    UUID guildId,
    UUID creatorId,
    int maxUses,
    LocalDateTime expiresAt
) {

}

