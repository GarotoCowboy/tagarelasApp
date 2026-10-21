package br.com.tagarelas.services.input;

import java.util.Set;
import java.util.UUID;

import br.com.tagarelas.core.enums.GuildPermission;

public record CreateRoleInput(
    UUID guildId,
    UUID requesterId,
    String name,
    String colorHex,
    int position,
    Set<GuildPermission> permissions
) {

}

