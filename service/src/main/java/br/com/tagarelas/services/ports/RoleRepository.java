package br.com.tagarelas.services.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.Role;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(UUID id);

    List<Role> findAllByGuildId(UUID guildId);

    void assignRoleToMember(UUID guildId, UUID userId, UUID roleId);

    void removeRoleFromMember(UUID guildId, UUID userId, UUID roleId);

    List<Role> findRolesByGuildAndUser(UUID guildId, UUID userId);
}

