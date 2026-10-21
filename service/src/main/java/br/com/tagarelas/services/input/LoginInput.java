package br.com.tagarelas.services.input;

public record LoginInput(
    String identifier,
    String rawPassword
) {

}

