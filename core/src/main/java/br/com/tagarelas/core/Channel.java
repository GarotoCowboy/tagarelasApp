package br.com.tagarelas.core;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.enums.MessageChannelType;
import br.com.tagarelas.core.exceptions.DomainValidationException;

public abstract class Channel {
    private UUID id;
    private MessageChannelType channelType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

     protected Channel(MessageChannelType channelType) {
        if (channelType == null) {
            throw new DomainValidationException("ChannelType cannot be null");
        }
        this.id = UUID.randomUUID();
        this.channelType = channelType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

      protected Channel(UUID id, MessageChannelType channelType, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted) {
        this.id = id;
        this.channelType = channelType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }
    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    protected void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    
    public UUID getId() { return id; }
    public MessageChannelType getChannelType() { return channelType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isDeleted() { return isDeleted; }

}
