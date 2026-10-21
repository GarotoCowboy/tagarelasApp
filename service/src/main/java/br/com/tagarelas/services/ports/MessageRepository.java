package br.com.tagarelas.services.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.Message;
import br.com.tagarelas.services.input.EditMessageInput;

public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAllByChannelId(UUID channelId);

    Optional<Message> edit(UUID id, EditMessageInput input);
}
