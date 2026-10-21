package br.com.tagarelas.services.output;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.enums.MessageChannelType;

public record DmChannelOutput(
    UUID id,
    UUID userOneId,
    UUID userTwoId,
    MessageChannelType channelType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isDeleted
) {

}

