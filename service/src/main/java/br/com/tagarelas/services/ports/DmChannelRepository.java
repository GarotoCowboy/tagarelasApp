package br.com.tagarelas.services.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.DmChannel;

public interface DmChannelRepository {

    DmChannel save(DmChannel dmChannel);

    Optional<DmChannel> findById(UUID id);

    Optional<DmChannel> findBetweenUsers(UUID userOneId, UUID userTwoId);

    List<DmChannel> findAllByUserId(UUID userId);
}

