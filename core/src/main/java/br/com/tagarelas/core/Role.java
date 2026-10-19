package br.com.tagarelas.core;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import br.com.tagarelas.core.enums.GuildPermission;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;

public class Role {
    private UUID id;
    private UUID guildId;
    private String name;
    private String colorHex;
    private int position;
    private Set<GuildPermission> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    public Role() {
        this.permissions = new HashSet<>();
    }

    public Role(UUID guildId, String name, String colorHex, int position, Set<GuildPermission> permissions) {
        validateGuildId(guildId);
        validateName(name);
        validateColorHex(colorHex);
        validatePosition(position);

        this.id = UUID.randomUUID();
        this.guildId = guildId;
        this.name = name;
        this.colorHex = colorHex;
        this.position = position;
        this.permissions = permissions != null ? new HashSet<>(permissions) : new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    public Role(UUID id, UUID guildId, String name, String colorHex, int position, Set<GuildPermission> permissions,
            LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted) {
        this.id = id;
        this.guildId = guildId;
        this.name = name;
        this.colorHex = colorHex;
        this.position = position;
        this.permissions = permissions != null ? new HashSet<>(permissions) : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    private void validateGuildId(UUID guildId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID is mandatory for a Role");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("Role name cannot be null or blank");
        }
        if (name.length() > 50) {
            throw new DomainValidationException("Role name cannot exceed 50 characters");
        }
    }

    private void validateColorHex(String colorHex) {
        if (colorHex != null && !colorHex.matches("^#([A-Fa-f0-9]{6})$")) {
            throw new DomainValidationException("Invalid color hex format (must be #RRGGBB, e.g. #FF5733)");
        }
    }

    private void validatePosition(int position) {
        if (position < 0) {
            throw new DomainValidationException("Role position cannot be negative");
        }
    }

    public boolean hasPermission(GuildPermission permission) {
        if (this.permissions.contains(GuildPermission.ADMINISTRATOR)) {
            return true; 
        }
        return this.permissions.contains(permission);
    }

    public void addPermission(GuildPermission permission) {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Cannot modify a deleted role");
        }
        if (permission == null) {
            throw new DomainValidationException("Permission cannot be null");
        }
        this.permissions.add(permission);
        this.updatedAt = LocalDateTime.now();
    }

    public void removePermission(GuildPermission permission) {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Cannot modify a deleted role");
        }
        this.permissions.remove(permission);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDetails(String name, String colorHex) {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Cannot update a deleted role");
        }
        validateName(name);
        validateColorHex(colorHex);
        this.name = name;
        this.colorHex = colorHex;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePosition(int newPosition) {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Cannot change position of a deleted role");
        }
        validatePosition(newPosition);
        this.position = newPosition;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Role is already deleted");
        }
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getGuildId() {
        return guildId;
    }

    public String getName() {
        return name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public int getPosition() {
        return position;
    }

    public Set<GuildPermission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}

