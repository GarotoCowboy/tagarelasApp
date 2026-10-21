package br.com.tagarelas.services.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.GuildChannel;

public interface GuildChannelRepository {

    GuildChannel save(GuildChannel guildChannel);

    Optional<GuildChannel> findById(UUID id);

    List<GuildChannel> findAllByGuildId(UUID guildId);
}

