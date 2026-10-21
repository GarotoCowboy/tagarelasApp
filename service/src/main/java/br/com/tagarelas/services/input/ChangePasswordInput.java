package br.com.tagarelas.services.input;

public record ChangePasswordInput(
    String currentPassword,
    String newPassword
) {

}

