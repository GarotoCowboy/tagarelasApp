package br.com.tagarelas.services.input;

import java.util.Set;
import java.util.UUID;

public record CreateGroupInput(
    String name,
    String description,
    String iconUrl,
    UUID ownerId,
    Set<UUID> initialMembers
) {

}

