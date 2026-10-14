package br.com.tagarelas.core;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.tagarelas.core.enums.MessageType;

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




}
