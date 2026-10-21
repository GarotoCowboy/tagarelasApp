package br.com.tagarelas.services.output;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.enums.FriendShipStatus;

public record FriendshipOutput(
    UUID id,
    UUID requesterId,
    UUID addresseeId,
    FriendShipStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}

