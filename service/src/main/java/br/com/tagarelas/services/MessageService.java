package br.com.tagarelas.services;

import java.util.UUID;

import br.com.tagarelas.core.Message;
import br.com.tagarelas.core.exceptions.DomainValidationException;
import br.com.tagarelas.core.exceptions.ErrorCode;
import br.com.tagarelas.core.exceptions.MessageException;
import br.com.tagarelas.services.input.EditMessageInput;
import br.com.tagarelas.services.input.SaveMessageInput;
import br.com.tagarelas.services.output.MessageOutput;
import br.com.tagarelas.services.ports.MessageRepository;

public class MessageService {
    private final MessageRepository repository;

    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public MessageOutput save(SaveMessageInput input) {
        if (input == null) {
            throw new DomainValidationException("Input cannot be null");
        }

        if (input.senderId() == null || input.channelId() == null) {
            throw new DomainValidationException("SenderId and ChannelId cannot be null");
        }

        if (input.content() != null && input.content().length() > 2000) {
            throw new MessageException(ErrorCode.MESSAGE_CONTENT_TOO_LONG,
                    "Message content cannot exceed 2000 characters");
        }

        Message toSave = new Message(
                input.messageType(),
                input.content(),
                input.attachments(),
                input.senderId(),
                input.channelId());

        Message savedMessage = repository.save(toSave);

        return toOutput(savedMessage);
    }

    public MessageOutput findById(UUID id) {
        if (id == null) {
            throw new DomainValidationException("Id cannot be null");
        }

        Message message = repository.findById(id)
                .orElseThrow(
                        () -> new MessageException(ErrorCode.MESSAGE_NOT_FOUND, "Cannot find a message with id:" + id));

        return toOutput(message);
    }

    public void delete(UUID id) {
        if (id == null) {
            throw new DomainValidationException("Id cannot be null");
        }
        Message message = repository.findById(id)
                .orElseThrow(
                        () -> new MessageException(ErrorCode.MESSAGE_NOT_FOUND, "Cannot find a message with id:" + id));

        message.delete();
        repository.save(message);
    }

    public MessageOutput edit(UUID id, EditMessageInput input) {
        if (id == null) {
            throw new DomainValidationException("Id cannot be null");
        }
        if (input == null) {
            throw new DomainValidationException("Input cannot be null");
        }

        if (input.content() != null && input.content().length() > 2000) {
            throw new MessageException(ErrorCode.MESSAGE_CONTENT_TOO_LONG,
                    "Message content cannot exceed 2000 characters");
        }

        boolean hasContent = input.content() != null && !input.content().isBlank();
        boolean hasAttachments = input.attachments() != null && !input.attachments().isEmpty();

        if (!hasContent && !hasAttachments) {
            throw new DomainValidationException("At least content or attachments must be provided to edit");
        }

        Message message = repository.findById(id)
                .orElseThrow(
                        () -> new MessageException(ErrorCode.MESSAGE_NOT_FOUND, "Cannot find a message with id:" + id));

        if (input.content() != null) {
            message.editContent(input.content());
        }

        if (input.attachments() != null) {
            message.setAttachments(input.attachments());
        }

        Message savedMessage = repository.save(message);

        return toOutput(savedMessage);
    }

    public MessageOutput toOutput(Message message) {

        return new MessageOutput(
                message.getId(),
                message.getMessageType(),
                message.getContent(),
                message.getAttachments(),
                message.getSenderId(),
                message.getChannelId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.isDeleted());
    }
}
