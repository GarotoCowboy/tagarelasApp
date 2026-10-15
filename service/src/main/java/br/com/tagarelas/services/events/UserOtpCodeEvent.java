package br.com.tagarelas.services.events;

import java.util.UUID;

public record UserOtpCodeEvent(
    UUID userId,
    String otpCode,
    String email,
    String phoneNumber
) {

}
