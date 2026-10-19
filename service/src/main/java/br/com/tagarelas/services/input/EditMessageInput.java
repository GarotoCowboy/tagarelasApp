package br.com.tagarelas.services.input;

import java.util.List;

import br.com.tagarelas.core.Attachment;
import br.com.tagarelas.core.enums.MessageType;

public record EditMessageInput(
       MessageType messageType,
        String content,
        List<Attachment> attachments
) {
 
}
