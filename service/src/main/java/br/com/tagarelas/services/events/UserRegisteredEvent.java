package br.com.tagarelas.services.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegisteredEvent(
    UUID userId,
    String email,
    String phoneNumber,
    LocalDateTime occuredOn
) {

}
