package br.com.tagarelas.core;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;

public class GuildInvite {
    private UUID id;
    private String code;
    private UUID guildId;
    private UUID creatorId;
    private int maxUses; // 0 significa usos ilimitados
    private int currentUses;
    private LocalDateTime expiresAt; // null significa que nunca expira
    private LocalDateTime createdAt;
    private boolean isDeleted;

    public GuildInvite() {
    }

    public GuildInvite(String code, UUID guildId, UUID creatorId, int maxUses, LocalDateTime expiresAt) {
        validateCode(code);
        validateGuildId(guildId);
        validateCreatorId(creatorId);
        validateMaxUses(maxUses);
        validateExpiresAt(expiresAt);

        this.id = UUID.randomUUID();
        this.code = code;
        this.guildId = guildId;
        this.creatorId = creatorId;
        this.maxUses = maxUses;
        this.currentUses = 0;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    public GuildInvite(UUID id, String code, UUID guildId, UUID creatorId, int maxUses, int currentUses,
            LocalDateTime expiresAt, LocalDateTime createdAt, boolean isDeleted) {
        this.id = id;
        this.code = code;
        this.guildId = guildId;
        this.creatorId = creatorId;
        this.maxUses = maxUses;
        this.currentUses = currentUses;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    private void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new DomainValidationException("Invite code cannot be null or blank");
        }
    }

    private void validateGuildId(UUID guildId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID is mandatory for an invite");
        }
    }

    private void validateCreatorId(UUID creatorId) {
        if (creatorId == null) {
            throw new DomainValidationException("Creator ID is mandatory for an invite");
        }
    }

    private void validateMaxUses(int maxUses) {
        if (maxUses < 0) {
            throw new DomainValidationException("Max uses cannot be negative");
        }
    }

    private void validateExpiresAt(LocalDateTime expiresAt) {
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw new DomainValidationException("Expiration date cannot be in the past");
        }
    }

    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean hasReachedMaxUses() {
        return this.maxUses > 0 && this.currentUses >= this.maxUses;
    }

    public boolean canBeUsed() {
        return !this.isDeleted && !isExpired() && !hasReachedMaxUses();
    }

    public void use() {
        if (!canBeUsed()) {
            throw new BusinessRuleViolationException("This invite link is invalid, expired, or has reached max uses");
        }
        this.currentUses++;
    }

    public void delete() {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Invite is already deleted");
        }
        this.isDeleted = true;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public UUID getGuildId() {
        return guildId;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public int getCurrentUses() {
        return currentUses;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}

