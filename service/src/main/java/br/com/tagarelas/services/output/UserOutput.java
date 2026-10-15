package br.com.tagarelas.services.output;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.enums.UserStatus;

public record UserOutput(
    UUID id,
    String username,
    String fullname,
    String email,
    String phoneNumber,
    UserStatus status,
    LocalDateTime createdAt
) {

}
