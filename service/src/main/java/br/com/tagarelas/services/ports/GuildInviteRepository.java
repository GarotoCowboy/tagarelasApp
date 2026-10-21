package br.com.tagarelas.services.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.GuildInvite;

public interface GuildInviteRepository {

    GuildInvite save(GuildInvite invite);

    Optional<GuildInvite> findById(UUID id);

    Optional<GuildInvite> findByCode(String code);

    List<GuildInvite> findAllByGuildId(UUID guildId);
}

