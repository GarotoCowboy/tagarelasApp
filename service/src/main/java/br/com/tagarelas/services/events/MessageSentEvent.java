package br.com.tagarelas.services.events;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.enums.MessageType;

public record MessageSentEvent(
    UUID messageId,
    UUID channelId,
    UUID senderId,
    MessageType messageType,
    String content,
    LocalDateTime createdAt
) {

}

