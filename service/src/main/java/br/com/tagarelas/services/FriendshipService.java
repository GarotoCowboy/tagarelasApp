package br.com.tagarelas.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.Friendship;
import br.com.tagarelas.core.User;
import br.com.tagarelas.core.enums.FriendShipStatus;
import br.com.tagarelas.core.enums.UserStatus;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;
import br.com.tagarelas.core.exceptions.ErrorCode;
import br.com.tagarelas.core.exceptions.FriendshipException;
import br.com.tagarelas.core.exceptions.UserException;
import br.com.tagarelas.services.input.SendFriendRequestInput;
import br.com.tagarelas.services.output.FriendshipOutput;
import br.com.tagarelas.services.ports.FriendshipRepository;
import br.com.tagarelas.services.ports.UserRepository;

public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public FriendshipOutput sendFriendRequest(SendFriendRequestInput input) {
        if (input == null) {
            throw new DomainValidationException("Input cannot be null");
        }
        if (input.requesterId() == null || input.addresseeId() == null) {
            throw new DomainValidationException("Requester and Addressee IDs are mandatory");
        }
        if (input.requesterId().equals(input.addresseeId())) {
            throw new DomainValidationException("You cannot send a friend request to yourself");
        }

        User requester = userRepository.findById(input.requesterId())
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID,
                        "Requester user not found with id: " + input.requesterId()));

        User addressee = userRepository.findById(input.addresseeId())
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID,
                        "Addressee user not found with id: " + input.addresseeId()));

        if (requester.getUserStatus() != UserStatus.ACTIVE || addressee.getUserStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Both users must have an active account to establish a friendship");
        }

        Optional<Friendship> existingFriendship = friendshipRepository.findBetweenUsers(
                input.requesterId(), input.addresseeId());

        if (existingFriendship.isPresent()) {
            FriendShipStatus currentStatus = existingFriendship.get().getStatus();
            if (currentStatus == FriendShipStatus.BLOCKED) {
                throw new FriendshipException(ErrorCode.FRIENDSHIP_BLOCKED);
            }
            if (currentStatus == FriendShipStatus.PENDING || currentStatus == FriendShipStatus.ACCEPTED) {
                throw new FriendshipException(ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
            }
        }

        Friendship friendship = new Friendship(input.requesterId(), input.addresseeId());
        Friendship savedFriendship = friendshipRepository.save(friendship);

        return toOutput(savedFriendship);
    }

    public FriendshipOutput acceptFriendRequest(UUID friendshipId, UUID currentUserId) {
        validateIds(friendshipId, currentUserId);

        Friendship friendship = findEntityById(friendshipId);
        friendship.accept(currentUserId);

        Friendship savedFriendship = friendshipRepository.save(friendship);
        return toOutput(savedFriendship);
    }

    public FriendshipOutput declineFriendRequest(UUID friendshipId, UUID currentUserId) {
        validateIds(friendshipId, currentUserId);

        Friendship friendship = findEntityById(friendshipId);
        friendship.decline(currentUserId);

        Friendship savedFriendship = friendshipRepository.save(friendship);
        return toOutput(savedFriendship);
    }

    public FriendshipOutput blockUser(UUID friendshipId, UUID currentUserId) {
        validateIds(friendshipId, currentUserId);

        Friendship friendship = findEntityById(friendshipId);
        friendship.block(currentUserId);

        Friendship savedFriendship = friendshipRepository.save(friendship);
        return toOutput(savedFriendship);
    }

    public void removeFriendship(UUID friendshipId, UUID currentUserId) {
        validateIds(friendshipId, currentUserId);

        Friendship friendship = findEntityById(friendshipId);

        if (!friendship.getRequesterId().equals(currentUserId) && !friendship.getAddresseeId().equals(currentUserId)) {
            throw new BusinessRuleViolationException("You are not part of this friendship");
        }

        friendshipRepository.delete(friendshipId);
    }

    public FriendshipOutput findById(UUID friendshipId) {
        if (friendshipId == null) {
            throw new DomainValidationException("Friendship ID cannot be null");
        }
        return toOutput(findEntityById(friendshipId));
    }

    public List<FriendshipOutput> listAcceptedFriends(UUID userId) {
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        return friendshipRepository.findAllByUserIdAndStatus(userId, FriendShipStatus.ACCEPTED)
                .stream()
                .map(this::toOutput)
                .toList();
    }

    public List<FriendshipOutput> listPendingRequests(UUID userId) {
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        return friendshipRepository.findAllByUserIdAndStatus(userId, FriendShipStatus.PENDING)
                .stream()
                .map(this::toOutput)
                .toList();
    }

    public List<FriendshipOutput> listAllByUser(UUID userId) {
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        return friendshipRepository.findAllByUserId(userId)
                .stream()
                .map(this::toOutput)
                .toList();
    }

    private Friendship findEntityById(UUID friendshipId) {
        return friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new FriendshipException(ErrorCode.FRIENDSHIP_NOT_FOUND,
                        "Friendship not found with id: " + friendshipId));
    }

    private void validateIds(UUID friendshipId, UUID currentUserId) {
        if (friendshipId == null) {
            throw new DomainValidationException("Friendship ID cannot be null");
        }
        if (currentUserId == null) {
            throw new DomainValidationException("Current user ID cannot be null");
        }
    }

    private FriendshipOutput toOutput(Friendship friendship) {
        return new FriendshipOutput(
                friendship.getId(),
                friendship.getRequesterId(),
                friendship.getAddresseeId(),
                friendship.getStatus(),
                friendship.getCreatedAt(),
                friendship.getUpdatedAt());
    }
}

