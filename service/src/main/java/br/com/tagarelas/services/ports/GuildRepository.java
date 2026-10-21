package br.com.tagarelas.services.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.Guild;

public interface GuildRepository {

    Guild save(Guild guild);

    Optional<Guild> findById(UUID id);

    List<Guild> findAllByMemberId(UUID userId);
}

