package br.com.tagarelas.services;

import java.util.List;
import java.util.UUID;

import br.com.tagarelas.core.GroupChannel;
import br.com.tagarelas.core.User;
import br.com.tagarelas.core.enums.UserStatus;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;
import br.com.tagarelas.core.exceptions.ErrorCode;
import br.com.tagarelas.core.exceptions.GroupException;
import br.com.tagarelas.core.exceptions.UserException;
import br.com.tagarelas.services.input.CreateGroupInput;
import br.com.tagarelas.services.input.UpdateGroupInput;
import br.com.tagarelas.services.output.GroupOutput;
import br.com.tagarelas.services.ports.GroupRepository;
import br.com.tagarelas.services.ports.UserRepository;

public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public GroupOutput create(CreateGroupInput input) {
        if (input == null) {
            throw new DomainValidationException("Input cannot be null");
        }
        if (input.ownerId() == null) {
            throw new DomainValidationException("Owner ID cannot be null");
        }

        User owner = userRepository.findById(input.ownerId())
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID,
                        "Owner user not found with id: " + input.ownerId()));

        if (owner.getUserStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only active users can create a group");
        }

        GroupChannel group = new GroupChannel(
                input.name(),
                input.description(),
                input.iconUrl(),
                input.ownerId());

        if (input.initialMembers() != null && !input.initialMembers().isEmpty()) {
            for (UUID memberId : input.initialMembers()) {
                if (memberId == null) {
                    throw new DomainValidationException("Member ID cannot be null");
                }
                if (memberId.equals(input.ownerId())) {
                    continue;
                }
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID,
                                "User not found with id: " + memberId));

                if (member.getUserStatus() != UserStatus.ACTIVE) {
                    throw new BusinessRuleViolationException("Only active users can be added to a group");
                }
                group.addMember(memberId);
            }
        }

        GroupChannel savedGroup = groupRepository.save(group);
        return toOutput(savedGroup);
    }

    public GroupOutput findById(UUID groupId) {
        if (groupId == null) {
            throw new DomainValidationException("Group ID cannot be null");
        }
        return toOutput(findGroupEntityById(groupId));
    }

    public List<GroupOutput> listByMember(UUID userId) {
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        return groupRepository.findAllByMemberId(userId)
                .stream()
                .map(this::toOutput)
                .toList();
    }

    public GroupOutput update(UUID groupId, UUID requesterId, UpdateGroupInput input) {
        if (groupId == null) {
            throw new DomainValidationException("Group ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }
        if (input == null) {
            throw new DomainValidationException("Input cannot be null");
        }

        boolean hasName = input.name() != null && !input.name().isBlank();
        boolean hasDescription = input.description() != null;
        boolean hasIconUrl = input.iconUrl() != null;

        if (!hasName && !hasDescription && !hasIconUrl) {
            throw new DomainValidationException("At least one field must be provided to update the group");
        }

        GroupChannel group = findGroupEntityById(groupId);

        if (!group.isOwner(requesterId)) {
            throw new GroupException(ErrorCode.GROUP_UNAUTHORIZED_ACTION,
                    "Only the group owner can update group details");
        }

        String updatedName = hasName ? input.name() : group.getName();
        String updatedDescription = hasDescription ? input.description() : group.getDescription();
        String updatedIconUrl = hasIconUrl ? input.iconUrl() : group.getIconUrl();

        group.updateDetails(updatedName, updatedDescription, updatedIconUrl);

        GroupChannel savedGroup = groupRepository.save(group);
        return toOutput(savedGroup);
    }

    public void delete(UUID groupId, UUID requesterId) {
        if (groupId == null) {
            throw new DomainValidationException("Group ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        GroupChannel group = findGroupEntityById(groupId);

        if (!group.isOwner(requesterId)) {
            throw new GroupException(ErrorCode.GROUP_UNAUTHORIZED_ACTION,
                    "Only the group owner can delete the group");
        }

        group.delete();
        groupRepository.save(group);
    }

    public GroupOutput addMember(UUID groupId, UUID requesterId, UUID newMemberId) {
        if (groupId == null) {
            throw new DomainValidationException("Group ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }
        if (newMemberId == null) {
            throw new DomainValidationException("New member ID cannot be null");
        }

        GroupChannel group = findGroupEntityById(groupId);

        if (!group.hasMember(requesterId)) {
            throw new GroupException(ErrorCode.GROUP_UNAUTHORIZED_ACTION,
                    "Only members of the group can add new members");
        }

        User newMember = userRepository.findById(newMemberId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID,
                        "User not found with id: " + newMemberId));

        if (newMember.getUserStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only active users can be added to a group");
        }

        group.addMember(newMemberId);

        GroupChannel savedGroup = groupRepository.save(group);
        return toOutput(savedGroup);
    }

    public GroupOutput removeMember(UUID groupId, UUID requesterId, UUID targetUserId) {
        if (groupId == null) {
            throw new DomainValidationException("Group ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }
        if (targetUserId == null) {
            throw new DomainValidationException("Target user ID cannot be null");
        }

        GroupChannel group = findGroupEntityById(groupId);

        boolean isSelfLeave = requesterId.equals(targetUserId);
        if (!isSelfLeave && !group.isOwner(requesterId)) {
            throw new GroupException(ErrorCode.GROUP_UNAUTHORIZED_ACTION,
                    "Only the group owner can remove another member");
        }

        group.removeMember(targetUserId);

        GroupChannel savedGroup = groupRepository.save(group);
        return toOutput(savedGroup);
    }

    public GroupOutput transferOwnership(UUID groupId, UUID currentOwnerId, UUID newOwnerId) {
        if (groupId == null) {
            throw new DomainValidationException("Group ID cannot be null");
        }
        if (currentOwnerId == null) {
            throw new DomainValidationException("Current owner ID cannot be null");
        }
        if (newOwnerId == null) {
            throw new DomainValidationException("New owner ID cannot be null");
        }

        GroupChannel group = findGroupEntityById(groupId);

        if (!group.isOwner(currentOwnerId)) {
            throw new GroupException(ErrorCode.GROUP_UNAUTHORIZED_ACTION,
                    "Only the current owner can transfer group ownership");
        }

        group.transferOwnership(newOwnerId);

        GroupChannel savedGroup = groupRepository.save(group);
        return toOutput(savedGroup);
    }

    private GroupChannel findGroupEntityById(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND,
                        "Group not found with id: " + groupId));
    }

    private GroupOutput toOutput(GroupChannel group) {
        return new GroupOutput(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getIconUrl(),
                group.getOwnerId(),
                group.getMembers(),
                group.getChannelType(),
                group.getCreatedAt(),
                group.getUpdatedAt(),
                group.isDeleted());
    }
}

