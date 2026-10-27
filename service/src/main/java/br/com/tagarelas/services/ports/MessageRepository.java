package br.com.tagarelas.services.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.Message;

public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAllByChannelId(UUID channelId);
}
