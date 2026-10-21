package br.com.tagarelas.services.input;

import java.util.UUID;

public record SendFriendRequestInput(
    UUID requesterId,
    UUID addresseeId
) {

}

