package br.com.tagarelas.services;

import java.util.List;
import java.util.UUID;

import br.com.tagarelas.core.Guild;
import br.com.tagarelas.core.Role;
import br.com.tagarelas.core.enums.GuildPermission;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;
import br.com.tagarelas.core.exceptions.ErrorCode;
import br.com.tagarelas.core.exceptions.GuildException;
import br.com.tagarelas.core.exceptions.RoleException;
import br.com.tagarelas.services.input.CreateRoleInput;
import br.com.tagarelas.services.input.UpdateRoleInput;
import br.com.tagarelas.services.output.RoleOutput;
import br.com.tagarelas.services.ports.GuildRepository;
import br.com.tagarelas.services.ports.RoleRepository;

public class RoleService {

    private final RoleRepository roleRepository;
    private final GuildRepository guildRepository;

    public RoleService(RoleRepository roleRepository, GuildRepository guildRepository) {
        this.roleRepository = roleRepository;
        this.guildRepository = guildRepository;
    }

    public RoleOutput create(CreateRoleInput input) {
        if (input == null) {
            throw new DomainValidationException("Input cannot be null");
        }
        if (input.guildId() == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        if (input.requesterId() == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        Guild guild = findGuildById(input.guildId());

        if (guild.isDeleted()) {
            throw new BusinessRuleViolationException("Cannot create roles in a deleted guild");
        }

        validateCanManageRoles(guild, input.requesterId());

        Role role = new Role(
                input.guildId(),
                input.name(),
                input.colorHex(),
                input.position(),
                input.permissions());

        Role savedRole = roleRepository.save(role);
        return toOutput(savedRole);
    }

    public RoleOutput findById(UUID roleId) {
        if (roleId == null) {
            throw new DomainValidationException("Role ID cannot be null");
        }
        return toOutput(findRoleEntityById(roleId));
    }

    public List<RoleOutput> listByGuild(UUID guildId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        return roleRepository.findAllByGuildId(guildId)
                .stream()
                .map(this::toOutput)
                .toList();
    }

    public List<RoleOutput> listMemberRoles(UUID guildId, UUID userId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        return roleRepository.findRolesByGuildAndUser(guildId, userId)
                .stream()
                .map(this::toOutput)
                .toList();
    }

    public RoleOutput updateDetails(UUID roleId, UUID requesterId, UpdateRoleInput input) {
        if (roleId == null) {
            throw new DomainValidationException("Role ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }
        if (input == null) {
            throw new DomainValidationException("Input cannot be null");
        }

        boolean hasName = input.name() != null && !input.name().isBlank();
        boolean hasColor = input.colorHex() != null;

        if (!hasName && !hasColor) {
            throw new DomainValidationException("At least name or colorHex must be provided to update the role");
        }

        Role role = findRoleEntityById(roleId);
        Guild guild = findGuildById(role.getGuildId());

        validateCanManageRoles(guild, requesterId);

        String updatedName = hasName ? input.name() : role.getName();
        String updatedColor = hasColor ? input.colorHex() : role.getColorHex();

        role.updateDetails(updatedName, updatedColor);

        Role savedRole = roleRepository.save(role);
        return toOutput(savedRole);
    }

    public RoleOutput changePosition(UUID roleId, UUID requesterId, int newPosition) {
        if (roleId == null) {
            throw new DomainValidationException("Role ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        Role role = findRoleEntityById(roleId);
        Guild guild = findGuildById(role.getGuildId());

        validateCanManageRoles(guild, requesterId);

        role.changePosition(newPosition);
        Role savedRole = roleRepository.save(role);

        return toOutput(savedRole);
    }

    public RoleOutput addPermission(UUID roleId, UUID requesterId, GuildPermission permission) {
        if (roleId == null) {
            throw new DomainValidationException("Role ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        Role role = findRoleEntityById(roleId);
        Guild guild = findGuildById(role.getGuildId());

        validateCanManageRoles(guild, requesterId);

        role.addPermission(permission);
        Role savedRole = roleRepository.save(role);

        return toOutput(savedRole);
    }

    public RoleOutput removePermission(UUID roleId, UUID requesterId, GuildPermission permission) {
        if (roleId == null) {
            throw new DomainValidationException("Role ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        Role role = findRoleEntityById(roleId);
        Guild guild = findGuildById(role.getGuildId());

        validateCanManageRoles(guild, requesterId);

        role.removePermission(permission);
        Role savedRole = roleRepository.save(role);

        return toOutput(savedRole);
    }

    public void delete(UUID roleId, UUID requesterId) {
        if (roleId == null) {
            throw new DomainValidationException("Role ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        Role role = findRoleEntityById(roleId);
        Guild guild = findGuildById(role.getGuildId());

        validateCanManageRoles(guild, requesterId);

        role.delete();
        roleRepository.save(role);
    }

    public void assignRoleToMember(UUID guildId, UUID requesterId, UUID targetUserId, UUID roleId) {
        if (guildId == null || requesterId == null || targetUserId == null || roleId == null) {
            throw new DomainValidationException("Guild ID, Requester ID, Target User ID, and Role ID cannot be null");
        }

        Guild guild = findGuildById(guildId);
        validateCanManageRoles(guild, requesterId);

        if (!guild.hasMember(targetUserId)) {
            throw new BusinessRuleViolationException("Target user is not a member of this guild");
        }

        Role role = findRoleEntityById(roleId);
        if (!role.getGuildId().equals(guildId)) {
            throw new BusinessRuleViolationException("Role does not belong to this guild");
        }
        if (role.isDeleted()) {
            throw new BusinessRuleViolationException("Cannot assign a deleted role to a member");
        }

        roleRepository.assignRoleToMember(guildId, targetUserId, roleId);
    }

    public void removeRoleFromMember(UUID guildId, UUID requesterId, UUID targetUserId, UUID roleId) {
        if (guildId == null || requesterId == null || targetUserId == null || roleId == null) {
            throw new DomainValidationException("Guild ID, Requester ID, Target User ID, and Role ID cannot be null");
        }

        Guild guild = findGuildById(guildId);
        validateCanManageRoles(guild, requesterId);

        Role role = findRoleEntityById(roleId);
        if (!role.getGuildId().equals(guildId)) {
            throw new BusinessRuleViolationException("Role does not belong to this guild");
        }

        roleRepository.removeRoleFromMember(guildId, targetUserId, roleId);
    }

    private void validateCanManageRoles(Guild guild, UUID requesterId) {
        if (guild.isOwner(requesterId)) {
            return;
        }
        if (!guild.hasMember(requesterId)) {
            throw new RoleException(ErrorCode.ROLE_UNAUTHORIZED_ACTION);
        }

        boolean hasManageRolesPerm = roleRepository.findRolesByGuildAndUser(guild.getId(), requesterId)
                .stream()
                .filter(role -> !role.isDeleted())
                .anyMatch(role -> role.hasPermission(GuildPermission.MANAGE_ROLES));

        if (!hasManageRolesPerm) {
            throw new RoleException(ErrorCode.ROLE_UNAUTHORIZED_ACTION);
        }
    }

    private Role findRoleEntityById(UUID roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleException(ErrorCode.ROLE_NOT_FOUND,
                        "Role not found with id: " + roleId));
    }

    private Guild findGuildById(UUID guildId) {
        return guildRepository.findById(guildId)
                .orElseThrow(() -> new GuildException(ErrorCode.GUILD_NOT_FOUND,
                        "Guild not found with id: " + guildId));
    }

    private RoleOutput toOutput(Role role) {
        return new RoleOutput(
                role.getId(),
                role.getGuildId(),
                role.getName(),
                role.getColorHex(),
                role.getPosition(),
                role.getPermissions(),
                role.getCreatedAt(),
                role.getUpdatedAt(),
                role.isDeleted());
    }
}

