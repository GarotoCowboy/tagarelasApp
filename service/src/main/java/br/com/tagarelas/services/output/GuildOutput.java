package br.com.tagarelas.services.output;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record GuildOutput(
    UUID id,
    String name,
    String description,
    String iconUrl,
    UUID ownerId,
    Set<UUID> members,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isDeleted
) {

}

