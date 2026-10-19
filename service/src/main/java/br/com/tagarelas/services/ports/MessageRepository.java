package br.com.tagarelas.services.ports;

import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.Message;
import br.com.tagarelas.services.input.EditMessageInput;

public interface MessageRepository {

    Message save(Message message);
    Optional<Message> findById(UUID id); 
    Optional<Message> edit(UUID id, EditMessageInput input);
}
