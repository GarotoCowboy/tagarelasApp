package br.com.tagarelas.core;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import br.com.tagarelas.core.enums.MessageChannelType;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;

public class GroupChannel extends Channel {

    private String name;
    private String description;
    private String iconUrl;
    private UUID ownerId;
    private Set<UUID> members;
    private LocalDateTime deletedAt;

    public GroupChannel() {
        super(MessageChannelType.GROUP);
        this.members = new HashSet<>();
    }

    public GroupChannel(String name, String description, String iconUrl, UUID ownerId) {
        super(MessageChannelType.GROUP);
        validateName(name);
        validateOwnerId(ownerId);

        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.ownerId = ownerId;
        this.members = new HashSet<>();
        this.members.add(ownerId);
        this.deletedAt = null;
    }

    public GroupChannel(UUID id, String name, String description, String iconUrl, UUID ownerId, Set<UUID> members,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, boolean isDeleted) {
        super(id, MessageChannelType.GROUP, createdAt, updatedAt, isDeleted);
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.ownerId = ownerId;
        this.members = members != null ? new HashSet<>(members) : new HashSet<>();
        this.deletedAt = deletedAt;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("Group name cannot be null or blank");
        }
        if (name.length() > 100) {
            throw new DomainValidationException("Group name cannot exceed 100 characters");
        }
    }

    private void validateOwnerId(UUID ownerId) {
        if (ownerId == null) {
            throw new DomainValidationException("Owner ID is mandatory for a group");
        }
    }

    public void addMember(UUID userId) {
        if (isDeleted()) {
            throw new BusinessRuleViolationException("Cannot add members to a deleted group");
        }
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        if (this.members.contains(userId)) {
            throw new BusinessRuleViolationException("User is already a member of this group");
        }
        this.members.add(userId);
        touchUpdatedAt();
    }

    public void removeMember(UUID userId) {
        if (isDeleted()) {
            throw new BusinessRuleViolationException("Cannot remove members from a deleted group");
        }
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        if (userId.equals(this.ownerId)) {
            throw new BusinessRuleViolationException("Cannot remove the owner of the group. Transfer ownership first.");
        }
        if (!this.members.contains(userId)) {
            throw new BusinessRuleViolationException("User is not a member of this group");
        }
        this.members.remove(userId);
        touchUpdatedAt();
    }

    public void transferOwnership(UUID newOwnerId) {
        if (isDeleted()) {
            throw new BusinessRuleViolationException("Cannot transfer ownership of a deleted group");
        }
        validateOwnerId(newOwnerId);
        if (!this.members.contains(newOwnerId)) {
            throw new BusinessRuleViolationException("New owner must already be a member of the group");
        }
        this.ownerId = newOwnerId;
        touchUpdatedAt();
    }

    public void updateDetails(String name, String description, String iconUrl) {
        if (isDeleted()) {
            throw new BusinessRuleViolationException("Cannot update a deleted group");
        }
        validateName(name);
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        touchUpdatedAt();
    }

    @Override
    public void delete() {
        if (isDeleted()) {
            throw new BusinessRuleViolationException("Group is already deleted");
        }
        super.delete();
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isOwner(UUID userId) {
        return this.ownerId != null && this.ownerId.equals(userId);
    }

    public boolean hasMember(UUID userId) {
        return this.members.contains(userId);
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}

