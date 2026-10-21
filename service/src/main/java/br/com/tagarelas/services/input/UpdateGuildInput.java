package br.com.tagarelas.services.input;

public record UpdateGuildInput(
    String name,
    String description,
    String iconUrl
) {

}

