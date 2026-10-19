package br.com.tagarelas.services.input;

import java.util.List;
import java.util.UUID;

import br.com.tagarelas.core.Attachment;
import br.com.tagarelas.core.enums.MessageType;

public record SaveMessageInput(

     MessageType messageType,
     String content,
     List<Attachment> attachments,
     UUID senderId,
     UUID channelId
) {

}
