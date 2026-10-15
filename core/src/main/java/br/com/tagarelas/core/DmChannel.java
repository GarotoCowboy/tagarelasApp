package br.com.tagarelas.core;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.enums.MessageChannelType;
import br.com.tagarelas.core.exceptions.DomainValidationException;

public class DmChannel extends Channel {
    private UUID userOneId;
    private UUID userTwoId;

    public DmChannel() {
        super(MessageChannelType.DM);
    }

    public DmChannel(UUID userOneId, UUID userTwoId) {
        super(MessageChannelType.DM);
        validateUsers(userOneId, userTwoId);
        validateDistinctUsers(userOneId, userTwoId);

        this.userOneId = userOneId;
        this.userTwoId = userTwoId;
    }

    public DmChannel(UUID id, UUID userOneId, UUID userTwoId, LocalDateTime createdAt, LocalDateTime updatedAt,
            boolean isDeleted) {
        super(id, MessageChannelType.DM, createdAt, updatedAt, isDeleted);
        this.userOneId = userOneId;
        this.userTwoId = userTwoId;
    }

    private void validateUsers(UUID userOneId, UUID userTwoId) {
        if (userOneId == null || userTwoId == null) {
            throw new DomainValidationException("DM requires two users");
        }
    }

    private void validateDistinctUsers(UUID userOneId, UUID userTwoId) {
        if (userOneId.equals(userTwoId)) {
            throw new DomainValidationException("Cannot open a DM with yourself");
        }
    }

    public UUID getUserOneId() {
        return userOneId;
    }

    public UUID getUserTwoId() {
        return userTwoId;
    }

    public boolean hasUser(UUID userId) {
        if (userId == null) {
            return false;
        }
        return userOneId.equals(userId) || userTwoId.equals(userId);
    }
}
