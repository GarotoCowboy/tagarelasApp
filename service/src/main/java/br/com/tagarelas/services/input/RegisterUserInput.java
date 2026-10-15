package br.com.tagarelas.services.input;

import java.time.LocalDate;

import br.com.tagarelas.core.enums.OtpDeliveryMethod;

public record RegisterUserInput(
    String username,
    String rawPassword,
    String fullName,
    LocalDate birthday,
    String email,
    String phoneNumber,
    OtpDeliveryMethod otpDeliveryMethod
) {

}
