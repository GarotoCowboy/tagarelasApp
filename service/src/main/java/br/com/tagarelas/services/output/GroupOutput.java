package br.com.tagarelas.services.output;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import br.com.tagarelas.core.enums.MessageChannelType;

public record GroupOutput(
    UUID id,
    String name,
    String description,
    String iconUrl,
    UUID ownerId,
    Set<UUID> members,
    MessageChannelType channelType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isDeleted
) {

}

