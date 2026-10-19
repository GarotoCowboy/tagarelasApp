package br.com.tagarelas.services.output;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.enums.UserStatus;

public record UserOutput(
    UUID id,
    String username,
    String fullname,
    LocalDate birthday,
    String email,
    String photoURL,
    String phoneNumber,
    UserStatus status,
    LocalDateTime createdAt
) {

}
