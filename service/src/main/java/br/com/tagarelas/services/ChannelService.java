package br.com.tagarelas.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.DmChannel;
import br.com.tagarelas.core.Friendship;
import br.com.tagarelas.core.Guild;
import br.com.tagarelas.core.GuildChannel;
import br.com.tagarelas.core.User;
import br.com.tagarelas.core.enums.FriendShipStatus;
import br.com.tagarelas.core.enums.UserStatus;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.ChannelException;
import br.com.tagarelas.core.exceptions.DomainValidationException;
import br.com.tagarelas.core.exceptions.ErrorCode;
import br.com.tagarelas.core.exceptions.GuildException;
import br.com.tagarelas.core.exceptions.UserException;
import br.com.tagarelas.services.input.CreateGuildChannelInput;
import br.com.tagarelas.services.output.DmChannelOutput;
import br.com.tagarelas.services.output.GuildChannelOutput;
import br.com.tagarelas.services.ports.DmChannelRepository;
import br.com.tagarelas.services.ports.FriendshipRepository;
import br.com.tagarelas.services.ports.GuildChannelRepository;
import br.com.tagarelas.services.ports.GuildRepository;
import br.com.tagarelas.services.ports.UserRepository;

public class ChannelService {

    private final DmChannelRepository dmChannelRepository;
    private final GuildChannelRepository guildChannelRepository;
    private final GuildRepository guildRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public ChannelService(
            DmChannelRepository dmChannelRepository,
            GuildChannelRepository guildChannelRepository,
            GuildRepository guildRepository,
            FriendshipRepository friendshipRepository,
            UserRepository userRepository) {
        this.dmChannelRepository = dmChannelRepository;
        this.guildChannelRepository = guildChannelRepository;
        this.guildRepository = guildRepository;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public DmChannelOutput getOrCreateDm(UUID userOneId, UUID userTwoId) {
        if (userOneId == null || userTwoId == null) {
            throw new DomainValidationException("DM requires two non-null user IDs");
        }
        if (userOneId.equals(userTwoId)) {
            throw new DomainValidationException("Cannot open a DM with yourself");
        }

        User userOne = userRepository.findById(userOneId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID,
                        "User not found with id: " + userOneId));

        User userTwo = userRepository.findById(userTwoId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID,
                        "User not found with id: " + userTwoId));

        if (userOne.getUserStatus() != UserStatus.ACTIVE || userTwo.getUserStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Both users must have an active account to open a DM");
        }

        Optional<Friendship> friendship = friendshipRepository.findBetweenUsers(userOneId, userTwoId);
        if (friendship.isPresent() && friendship.get().getStatus() == FriendShipStatus.BLOCKED) {
            throw new ChannelException(ErrorCode.DM_BLOCKED_BY_FRIENDSHIP);
        }

        Optional<DmChannel> existingDm = dmChannelRepository.findBetweenUsers(userOneId, userTwoId);
        if (existingDm.isPresent() && !existingDm.get().isDeleted()) {
            return toDmOutput(existingDm.get());
        }

        DmChannel dmChannel = new DmChannel(userOneId, userTwoId);
        DmChannel savedDm = dmChannelRepository.save(dmChannel);

        return toDmOutput(savedDm);
    }

    public DmChannelOutput findDmById(UUID channelId) {
        if (channelId == null) {
            throw new DomainValidationException("Channel ID cannot be null");
        }
        DmChannel dm = dmChannelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ErrorCode.CHANNEL_NOT_FOUND,
                        "DM channel not found with id: " + channelId));
        return toDmOutput(dm);
    }

    public List<DmChannelOutput> listUserDms(UUID userId) {
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        return dmChannelRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDmOutput)
                .toList();
    }

    public GuildChannelOutput createGuildChannel(CreateGuildChannelInput input) {
        if (input == null) {
            throw new DomainValidationException("Input cannot be null");
        }
        if (input.guildId() == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        if (input.requesterId() == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        Guild guild = guildRepository.findById(input.guildId())
                .orElseThrow(() -> new GuildException(ErrorCode.GUILD_NOT_FOUND,
                        "Guild not found with id: " + input.guildId()));

        if (guild.isDeleted()) {
            throw new BusinessRuleViolationException("Cannot create a channel in a deleted guild");
        }

        if (!guild.isOwner(input.requesterId())) {
            throw new ChannelException(ErrorCode.CHANNEL_UNAUTHORIZED_ACTION,
                    "Only the guild owner can create channels");
        }

        GuildChannel guildChannel = new GuildChannel(
                input.guildId(),
                input.name(),
                input.kind());

        GuildChannel savedChannel = guildChannelRepository.save(guildChannel);
        return toGuildChannelOutput(savedChannel);
    }

    public GuildChannelOutput renameGuildChannel(UUID channelId, UUID requesterId, String newName) {
        if (channelId == null) {
            throw new DomainValidationException("Channel ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        GuildChannel channel = findGuildChannelEntityById(channelId);
        Guild guild = findGuildById(channel.getGuildId());

        if (!guild.isOwner(requesterId)) {
            throw new ChannelException(ErrorCode.CHANNEL_UNAUTHORIZED_ACTION,
                    "Only the guild owner can rename channels");
        }

        channel.rename(newName);
        GuildChannel savedChannel = guildChannelRepository.save(channel);

        return toGuildChannelOutput(savedChannel);
    }

    public GuildChannelOutput changeGuildChannelPosition(UUID channelId, UUID requesterId, int newPosition) {
        if (channelId == null) {
            throw new DomainValidationException("Channel ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        GuildChannel channel = findGuildChannelEntityById(channelId);
        Guild guild = findGuildById(channel.getGuildId());

        if (!guild.isOwner(requesterId)) {
            throw new ChannelException(ErrorCode.CHANNEL_UNAUTHORIZED_ACTION,
                    "Only the guild owner can reorder channels");
        }

        channel.changePosition(newPosition);
        GuildChannel savedChannel = guildChannelRepository.save(channel);

        return toGuildChannelOutput(savedChannel);
    }

    public void deleteGuildChannel(UUID channelId, UUID requesterId) {
        if (channelId == null) {
            throw new DomainValidationException("Channel ID cannot be null");
        }
        if (requesterId == null) {
            throw new DomainValidationException("Requester ID cannot be null");
        }

        GuildChannel channel = findGuildChannelEntityById(channelId);
        Guild guild = findGuildById(channel.getGuildId());

        if (!guild.isOwner(requesterId)) {
            throw new ChannelException(ErrorCode.CHANNEL_UNAUTHORIZED_ACTION,
                    "Only the guild owner can delete channels");
        }

        if (channel.isDeleted()) {
            throw new BusinessRuleViolationException("Channel is already deleted");
        }

        channel.delete();
        guildChannelRepository.save(channel);
    }

    public List<GuildChannelOutput> listGuildChannels(UUID guildId) {
        if (guildId == null) {
            throw new DomainValidationException("Guild ID cannot be null");
        }
        return guildChannelRepository.findAllByGuildId(guildId)
                .stream()
                .map(this::toGuildChannelOutput)
                .toList();
    }

    private GuildChannel findGuildChannelEntityById(UUID channelId) {
        return guildChannelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ErrorCode.CHANNEL_NOT_FOUND,
                        "Guild channel not found with id: " + channelId));
    }

    private Guild findGuildById(UUID guildId) {
        return guildRepository.findById(guildId)
                .orElseThrow(() -> new GuildException(ErrorCode.GUILD_NOT_FOUND,
                        "Guild not found with id: " + guildId));
    }

    private DmChannelOutput toDmOutput(DmChannel dm) {
        return new DmChannelOutput(
                dm.getId(),
                dm.getUserOneId(),
                dm.getUserTwoId(),
                dm.getChannelType(),
                dm.getCreatedAt(),
                dm.getUpdatedAt(),
                dm.isDeleted());
    }

    private GuildChannelOutput toGuildChannelOutput(GuildChannel channel) {
        return new GuildChannelOutput(
                channel.getId(),
                channel.getGuildId(),
                channel.getName(),
                channel.getKind(),
                channel.getPosition(),
                channel.getChannelType(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.isDeleted());
    }
}

