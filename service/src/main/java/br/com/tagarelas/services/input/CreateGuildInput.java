package br.com.tagarelas.services.input;

import java.util.UUID;

public record CreateGuildInput(
    String name,
    String description,
    String iconUrl,
    UUID ownerId
) {

}

