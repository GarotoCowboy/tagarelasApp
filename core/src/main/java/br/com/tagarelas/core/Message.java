package br.com.tagarelas.core;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.tagarelas.core.enums.MessageType;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;

public class Message {
    private UUID id;
    private MessageType messageType;
    private String content;
    private List<Attachment> attachments;
    private UUID senderId;
    private UUID channelId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    public Message() {
    }

    public Message(MessageType messageType, String content, List<Attachment> attachments, UUID senderId,
            UUID channelId) {
        validateMessageType(messageType);
        validateSenderId(senderId);
        validateChannelId(channelId);
        validateMessage(content, attachments);
        
        this.id = UUID.randomUUID();
        this.messageType = messageType;
        this.content = content;
        this.attachments = attachments;
        this.senderId = senderId;
        this.channelId = channelId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    public Message(UUID id, MessageType messageType, String content, List<Attachment> attachments, UUID senderId,
            UUID channelId, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted) {
        this.id = id;
        this.messageType = messageType;
        this.content = content;
        this.attachments = attachments;
        this.senderId = senderId;
        this.channelId = channelId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    private void validateMessageType(MessageType messageType) {
        if (messageType == null) {
            throw new DomainValidationException("MessageType cannot be null");
        }
    }

    private void validateSenderId(UUID senderId) {
        if (senderId == null) {
            throw new DomainValidationException("Sender ID is mandatory");
        }
    }

    private void validateChannelId(UUID channelId) {
        if (channelId == null) {
            throw new DomainValidationException("Channel ID is mandatory");
        }
    }

    private void validateMessage(String content, List<Attachment> attachments) {
        boolean hasText = content != null && !content.isBlank();
        boolean hasAttachments = attachments != null && !attachments.isEmpty();

        if (!hasText && !hasAttachments) {
            throw new DomainValidationException("Message must contain text or at least one attachment");
        }
    }

    public void editContent(String newContent) {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Cannot edit a deleted message");
        }
        validateMessage(newContent, this.attachments);
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Message is already deleted");
        }
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Cannot alter attachments of a deleted message");
        }
        validateMessage(this.content, attachments);
        this.updatedAt = LocalDateTime.now();
        this.attachments = attachments;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
