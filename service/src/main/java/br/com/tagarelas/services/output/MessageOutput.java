package br.com.tagarelas.services.output;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.tagarelas.core.Attachment;
import br.com.tagarelas.core.enums.MessageType;

public record MessageOutput(
    UUID id,
    MessageType messageType,
    String content,
    List<Attachment> attachments,
    UUID senderId,
    UUID channelId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isDeleted
) {

}
