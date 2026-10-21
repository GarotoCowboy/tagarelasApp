package br.com.tagarelas.services.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.Friendship;
import br.com.tagarelas.core.enums.FriendShipStatus;

public interface FriendshipRepository {

    Friendship save(Friendship friendship);

    Optional<Friendship> findById(UUID id);

    Optional<Friendship> findBetweenUsers(UUID userOneId, UUID userTwoId);

    List<Friendship> findAllByUserId(UUID userId);

    List<Friendship> findAllByUserIdAndStatus(UUID userId, FriendShipStatus status);

    void delete(UUID id);
}

