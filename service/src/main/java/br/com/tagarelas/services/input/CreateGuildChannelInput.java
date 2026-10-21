package br.com.tagarelas.services.input;

import java.util.UUID;

import br.com.tagarelas.core.enums.GuildChannelKind;

public record CreateGuildChannelInput(
    UUID guildId,
    UUID requesterId,
    String name,
    GuildChannelKind kind
) {

}

