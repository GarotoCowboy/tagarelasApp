package br.com.tagarelas.services;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

import br.com.tagarelas.core.Guild;
import br.com.tagarelas.core.GuildInvite;
import br.com.tagarelas.core.User;
import br.com.tagarelas.core.enums.GuildPermission;
import br.com.tagarelas.core.enums.UserStatus;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;
import br.com.tagarelas.core.exceptions.ErrorCode;
import br.com.tagarelas.core.exceptions.GuildException;
import br.com.tagarelas.core.exceptions.UserException;
import br.com.tagarelas.services.input.CreateGuildInput;
import br.com.tagarelas.services.input.CreateGuildInviteInput;
import br.com.tagarelas.services.input.UpdateGuildInput;
import br.com.tagarelas.services.output.GuildInviteOutput;
import br.com.tagarelas.services.output.GuildOutput;
import br.com.tagarelas.services.ports.GuildInviteRepository;
import br.com.tagarelas.services.ports.GuildRepository;
import br.com.tagarelas.services.ports.RoleRepository;
import br.com.tagarelas.services.ports.UserRepository;

public class GuildService {

    private static final String INVITE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_INVITE_CODE_LENGTH = 8;

    private final GuildRepository guildRepository;
    private final GuildInviteRepository guildInviteRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public GuildService(GuildRepository guildRepository, GuildInviteRepository guildInviteRepository,
            UserRepository userRepository) {
        this(guildRepository, guildInviteRepository, userRepository, null);
    }

    public GuildService(GuildRepository guildRepository, GuildInviteRepository guildInviteRepository,
            UserRepository userRepository, RoleRepository roleRepository) {
        this.guildRepository = guildRepository;
        this.guildInviteRepository = guildInviteRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public GuildOutput create(CreateGuildInput input) {
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
            throw new BusinessRuleViolationException("Only active users can create a guild");
        }

        Guild guild = new Guild(
                input.name(),
                input.description(),
                input.iconUrl(),
                input.ownerId());

        Guild savedGuild = guildRepository.save(guild);
        return toOutput(savedGuild);
    }

    public GuildOutput findById(UUID guildId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        return toOutput(findGuildEntityById(guildId));
    }

    public List<GuildOutput> listByMember(UUID userId) {
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        return guildRepository.findAllByMemberId(userId)
                .stream()
                .map(this::toOutput)
                .toList();
    }

    public GuildOutput update(UUID guildId, UUID requesterId, UpdateGuildInput input) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID cannot be null");
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
            throw new DomainValidationException("At least one field must be provided to update the guild");
        }

        Guild guild = findGuildEntityById(guildId);

        if (!hasGuildPermission(guild, requesterId, GuildPermission.MANAGE_GUILD)) {
            throw new GuildException(ErrorCode.GUILD_UNAUTHORIZED_ACTION,
                    "You do not have permission to update guild details");
        }

        String updatedName = hasName ? input.name() : guild.getName();
        String updatedDescription = hasDescription ? input.description() : guild.getDescription();
        String updatedIconUrl = hasIconUrl ? input.iconUrl() : guild.getIconUrl();

        guild.updateDetails(updatedName, updatedDescription, updatedIconUrl);

        Guild savedGuild = guildRepository.save(guild);
        return toOutput(savedGuild);
    }

    public void delete(UUID guildId, UUID requesterId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        Guild guild = findGuildEntityById(guildId);

        if (!guild.isOwner(requesterId)) {
            throw new GuildException(ErrorCode.GUILD_UNAUTHORIZED_ACTION,
                    "Only the guild owner can delete the guild");
        }

        guild.delete();
        guildRepository.save(guild);
    }

    public GuildOutput addMember(UUID guildId, UUID userId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID,
                        "User not found with id: " + userId));

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only active users can join a guild");
        }

        Guild guild = findGuildEntityById(guildId);
        guild.addMember(userId);

        Guild savedGuild = guildRepository.save(guild);
        return toOutput(savedGuild);
    }

    public GuildOutput removeMember(UUID guildId, UUID requesterId, UUID targetUserId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }
        if (targetUserId == null) {
            throw new DomainValidationException("Target user ID cannot be null");
        }

        Guild guild = findGuildEntityById(guildId);

        boolean isSelfLeave = requesterId.equals(targetUserId);
        if (!isSelfLeave && !hasGuildPermission(guild, requesterId, GuildPermission.KICK_MEMBERS)) {
            throw new GuildException(ErrorCode.GUILD_UNAUTHORIZED_ACTION,
                    "You do not have permission to kick members from this guild");
        }

        guild.removeMember(targetUserId);

        Guild savedGuild = guildRepository.save(guild);
        return toOutput(savedGuild);
    }

    public GuildOutput transferOwnership(UUID guildId, UUID currentOwnerId, UUID newOwnerId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        if (currentOwnerId == null) {
            throw new DomainValidationException("Current owner ID cannot be null");
        }
        if (newOwnerId == null) {
            throw new DomainValidationException("New owner ID cannot be null");
        }

        Guild guild = findGuildEntityById(guildId);

        if (!guild.isOwner(currentOwnerId)) {
            throw new GuildException(ErrorCode.GUILD_UNAUTHORIZED_ACTION,
                    "Only the current owner can transfer guild ownership");
        }

        guild.transferOwnership(newOwnerId);

        Guild savedGuild = guildRepository.save(guild);
        return toOutput(savedGuild);
    }

    public GuildInviteOutput createInvite(CreateGuildInviteInput input) {
        if (input == null) {
            throw new DomainValidationException("Input cannot be null");
        }
        if (input.guildId() == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        if (input.creatorId() == null) {
            throw new DomainValidationException("Creator ID cannot be null");
        }

        Guild guild = findGuildEntityById(input.guildId());

        if (guild.isDeleted()) {
            throw new BusinessRuleViolationException("Cannot create an invite for a deleted guild");
        }
        if (!guild.hasMember(input.creatorId())) {
            throw new GuildException(ErrorCode.GUILD_UNAUTHORIZED_ACTION,
                    "Only members of the guild can create invites");
        }

        String code = generateInviteCode(DEFAULT_INVITE_CODE_LENGTH);

        GuildInvite invite = new GuildInvite(
                code,
                input.guildId(),
                input.creatorId(),
                input.maxUses(),
                input.expiresAt());

        GuildInvite savedInvite = guildInviteRepository.save(invite);
        return toInviteOutput(savedInvite);
    }

    public GuildOutput joinByInvite(String inviteCode, UUID userId) {
        if (inviteCode == null || inviteCode.isBlank()) {
            throw new DomainValidationException("Invite code cannot be null or blank");
        }
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID,
                        "User not found with id: " + userId));

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only active users can join a guild");
        }

        GuildInvite invite = guildInviteRepository.findByCode(inviteCode)
                .orElseThrow(() -> new GuildException(ErrorCode.GUILD_INVITE_NOT_FOUND,
                        "Guild invite not found with code: " + inviteCode));

        Guild guild = findGuildEntityById(invite.getGuildId());

        invite.use();
        guild.addMember(userId);

        guildInviteRepository.save(invite);
        Guild savedGuild = guildRepository.save(guild);

        return toOutput(savedGuild);
    }

    public void revokeInvite(UUID inviteId, UUID requesterId) {
        if (inviteId == null) {
            throw new DomainValidationException("Invite ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        GuildInvite invite = guildInviteRepository.findById(inviteId)
                .orElseThrow(() -> new GuildException(ErrorCode.GUILD_INVITE_NOT_FOUND,
                        "Guild invite not found with id: " + inviteId));

        Guild guild = findGuildEntityById(invite.getGuildId());

        if (!invite.getCreatorId().equals(requesterId)
                && !hasGuildPermission(guild, requesterId, GuildPermission.MANAGE_GUILD)) {
            throw new GuildException(ErrorCode.GUILD_UNAUTHORIZED_ACTION,
                    "Only the invite creator or a guild manager can revoke this invite");
        }

        invite.delete();
        guildInviteRepository.save(invite);
    }

    public List<GuildInviteOutput> listInvitesByGuild(UUID guildId, UUID requesterId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        Guild guild = findGuildEntityById(guildId);

        if (!guild.hasMember(requesterId)) {
            throw new GuildException(ErrorCode.GUILD_UNAUTHORIZED_ACTION,
                    "Only guild members can view guild invites");
        }

        return guildInviteRepository.findAllByGuildId(guildId)
                .stream()
                .map(this::toInviteOutput)
                .toList();
    }

    private boolean hasGuildPermission(Guild guild, UUID userId, GuildPermission permission) {
        if (guild.isOwner(userId)) {
            return true;
        }
        if (!guild.hasMember(userId) || roleRepository == null) {
            return false;
        }
        return roleRepository.findRolesByGuildAndUser(guild.getId(), userId)
                .stream()
                .filter(role -> !role.isDeleted())
                .anyMatch(role -> role.hasPermission(permission));
    }

    private Guild findGuildEntityById(UUID guildId) {
        return guildRepository.findById(guildId)
                .orElseThrow(() -> new GuildException(ErrorCode.GUILD_NOT_FOUND,
                        "Guild not found with id: " + guildId));
    }

    private String generateInviteCode(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(INVITE_CHARS.length());
            sb.append(INVITE_CHARS.charAt(index));
        }
        return sb.toString();
    }

    private GuildOutput toOutput(Guild guild) {
        return new GuildOutput(
                guild.getId(),
                guild.getName(),
                guild.getDescription(),
                guild.getIconUrl(),
                guild.getOwnerId(),
                guild.getMembers(),
                guild.getCreatedAt(),
                guild.getUpdatedAt(),
                guild.isDeleted());
    }

    private GuildInviteOutput toInviteOutput(GuildInvite invite) {
        return new GuildInviteOutput(
                invite.getId(),
                invite.getCode(),
                invite.getGuildId(),
                invite.getCreatorId(),
                invite.getMaxUses(),
                invite.getCurrentUses(),
                invite.getExpiresAt(),
                invite.getCreatedAt(),
                invite.isDeleted());
    }
}
