package br.com.tagarelas.services.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.GroupChannel;

public interface GroupRepository {

    GroupChannel save(GroupChannel group);

    Optional<GroupChannel> findById(UUID id);

    List<GroupChannel> findAllByMemberId(UUID userId);
}

