package br.com.tagarelas.services.ports;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository {

    void save(UUID userId, String code, long expirationInSeconds);
    void delete(UUID userId);
    Optional<String> get(UUID userId);
}
