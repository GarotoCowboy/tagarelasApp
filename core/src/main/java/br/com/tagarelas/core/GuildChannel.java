package br.com.tagarelas.core;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.enums.GuildChannelKind;
import br.com.tagarelas.core.enums.MessageChannelType;
import br.com.tagarelas.core.exceptions.DomainValidationException;

public class GuildChannel extends Channel {

    private UUID guildId;
    private String name;
    private GuildChannelKind kind;
    private int position;

    public GuildChannel() {
        super(MessageChannelType.GUILD);
    }

    public GuildChannel(UUID guildId, String name, GuildChannelKind kind) {
        super(MessageChannelType.GUILD);
        validateGuildId(guildId);
        validateName(name);
        validateKind(kind);

        this.guildId = guildId;
        this.name = name;
        this.kind = kind;
        this.position = 0;
    }

    public GuildChannel(UUID id, UUID guildId, String name, GuildChannelKind kind, int position,
            LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted) {
        super(id, MessageChannelType.GUILD, createdAt, updatedAt, isDeleted);
        this.guildId = guildId;
        this.name = name;
        this.kind = kind;
        this.position = position;
    }

    private void validateGuildId(UUID guildId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID is mandatory");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("Channel name cannot be null or blank");
        }
        if (name.length() > 100) {
            throw new DomainValidationException("Channel name cannot exceed 100 characters");
        }
    }

    private void validateKind(GuildChannelKind kind) {
        if (kind == null) {
            throw new DomainValidationException("GuildChannelKind (TEXT or VOICE) is mandatory");
        }
    }

    public void rename(String newName) {
        validateName(newName);
        this.name = newName;
    }

    public void changePosition(int newPosition) {
        if (newPosition < 0) {
            throw new DomainValidationException("Channel position cannot be negative");
        }
        this.position = newPosition;
    }

    public UUID getGuildId() {
        return guildId;
    }

    public String getName() {
        return name;
    }

    public GuildChannelKind getKind() {
        return kind;
    }

    public int getPosition() {
        return position;
    }
}
