package br.com.tagarelas.services.ports;

public interface PasswordEncoder {

    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
