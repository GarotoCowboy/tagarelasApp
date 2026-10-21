package br.com.tagarelas.services.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDeletedEvent(
    UUID messageId,
    UUID channelId,
    LocalDateTime deletedAt
) {

}

