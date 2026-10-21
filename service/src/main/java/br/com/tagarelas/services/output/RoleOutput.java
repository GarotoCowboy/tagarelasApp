package br.com.tagarelas.services.output;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import br.com.tagarelas.core.enums.GuildPermission;

public record RoleOutput(
    UUID id,
    UUID guildId,
    String name,
    String colorHex,
    int position,
    Set<GuildPermission> permissions,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isDeleted
) {

}

