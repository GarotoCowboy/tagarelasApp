package br.com.tagarelas.core;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.enums.FriendShipStatus;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;

public class Friendship {
private UUID id;
private UUID requesterId;
private UUID addresseeId;
private FriendShipStatus status;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;




public Friendship(UUID requesterId, UUID addresseeId) {

    validateIfIdsIsValid(requesterId,addresseeId);

    this.id = UUID.randomUUID();
    this.requesterId = requesterId;
    this.addresseeId = addresseeId;
    this.status = FriendShipStatus.PENDING;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();

    validateIfITrySendAInviteToMyself(requesterId);
}

public Friendship(UUID id, UUID requesterId, UUID addresseeId, FriendShipStatus status, LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    this.id = id;
    this.requesterId = requesterId;
    this.addresseeId = addresseeId;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
}

public Friendship() {
}



public void accept(UUID currentUserId){

    if(!this.addresseeId.equals(currentUserId)){
        throw new BusinessRuleViolationException("Only Receiver can accept pending orders");
    }

    if(this.status != FriendShipStatus.PENDING){
        throw new BusinessRuleViolationException("Only pending orders can be accepted");
    }
    this.status = FriendShipStatus.ACCEPTED;
    updatedAt = LocalDateTime.now();
}

public void decline(UUID currentUserId){

    if(!this.addresseeId.equals(currentUserId)){
        throw new BusinessRuleViolationException("Only Receiver can decline pending orders");
    }

    if(this.status != FriendShipStatus.PENDING){
        throw new BusinessRuleViolationException("Only pending orders can be declined");
    }
    this.status = FriendShipStatus.DECLINED;
    updatedAt = LocalDateTime.now();
}

public void block(UUID currentUserId) {
    if (!this.requesterId.equals(currentUserId) && !this.addresseeId.equals(currentUserId)) {
        throw new BusinessRuleViolationException("You are not part of this friendship");
    }
    this.status = FriendShipStatus.BLOCKED;
    this.updatedAt = LocalDateTime.now();
}


private void validateIfITrySendAInviteToMyself(UUID requesterId){
if(requesterId.equals(this.addresseeId)){
    throw new DomainValidationException("You cannot send a friend request to yourself");
}
}
private void validateIfIdsIsValid(UUID requesterId, UUID addresseeId){
    if (requesterId == null || addresseeId == null) {
    throw new DomainValidationException("Requester and Addressee IDs are mandatory");
}
}


public UUID getId() {
    return id;
}

public UUID getRequesterId() {
    return requesterId;
}

public UUID getAddresseeId() {
    return addresseeId;
}

public FriendShipStatus getStatus() {
    return status;
}

public LocalDateTime getCreatedAt() {
    return createdAt;
}

public LocalDateTime getUpdatedAt() {
    return updatedAt;
}










}
