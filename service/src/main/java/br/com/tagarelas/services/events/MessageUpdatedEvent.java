package br.com.tagarelas.services.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageUpdatedEvent(
    UUID messageId,
    UUID channelId,
    String content,
    LocalDateTime updatedAt
) {

}

