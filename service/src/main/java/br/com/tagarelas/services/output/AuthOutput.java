package br.com.tagarelas.services.output;

public record AuthOutput(
    String accessToken,
    UserOutput user
) {

}

