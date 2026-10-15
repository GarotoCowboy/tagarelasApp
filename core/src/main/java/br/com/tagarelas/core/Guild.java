package br.com.tagarelas.core;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;

public class Guild {
    private UUID id;
    private String name;
    private String description;
    private String iconUrl;
    private UUID ownerId;
    private Set<UUID> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;

    public Guild() {
        this.members = new HashSet<>();
    }

    public Guild(String name, String description, String iconUrl, UUID ownerId) {
        validateName(name);
        validateOwnerId(ownerId);

        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.ownerId = ownerId;
        this.members = new HashSet<>();
        this.members.add(ownerId); // O dono nasce como membro automaticamente!
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    public Guild(UUID id, String name, String description, String iconUrl, UUID ownerId, Set<UUID> members,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.ownerId = ownerId;
        this.members = members != null ? new HashSet<>(members) : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.isDeleted = isDeleted;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("Guild name cannot be null or blank");
        }
        if (name.length() > 100) {
            throw new DomainValidationException("Guild name cannot exceed 100 characters");
        }
    }

    private void validateOwnerId(UUID ownerId) {
        if (ownerId == null) {
            throw new DomainValidationException("Owner ID is mandatory");
        }
    }

    public void addMember(UUID userId) {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Cannot add members to a deleted guild");
        }
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        if (this.members.contains(userId)) {
            throw new BusinessRuleViolationException("User is already a member of this guild");
        }
        this.members.add(userId);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeMember(UUID userId) {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Cannot remove members from a deleted guild");
        }
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        if (userId.equals(this.ownerId)) {
            throw new BusinessRuleViolationException("Cannot remove the owner of the guild. Transfer ownership first.");
        }
        if (!this.members.contains(userId)) {
            throw new BusinessRuleViolationException("User is not a member of this guild");
        }
        this.members.remove(userId);
        this.updatedAt = LocalDateTime.now();
    }

    public void transferOwnership(UUID newOwnerId) {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Cannot transfer ownership of a deleted guild");
        }
        validateOwnerId(newOwnerId);
        if (!this.members.contains(newOwnerId)) {
            throw new BusinessRuleViolationException("New owner must already be a member of the guild");
        }
        this.ownerId = newOwnerId;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDetails(String name, String description, String iconUrl) {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Cannot update a deleted guild");
        }
        validateName(name);
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        if (this.isDeleted) {
            throw new BusinessRuleViolationException("Guild is already deleted");
        }
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOwner(UUID userId) {
        return this.ownerId != null && this.ownerId.equals(userId);
    }

    public boolean hasMember(UUID userId) {
        return this.members.contains(userId);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
