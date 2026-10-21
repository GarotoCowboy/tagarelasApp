package br.com.tagarelas.services.output;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.enums.GuildChannelKind;
import br.com.tagarelas.core.enums.MessageChannelType;

public record GuildChannelOutput(
    UUID id,
    UUID guildId,
    String name,
    GuildChannelKind kind,
    int position,
    MessageChannelType channelType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isDeleted
) {

}

