package br.com.tagarelas.application.adapters.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import br.com.tagarelas.core.User;
import br.com.tagarelas.services.ports.TokenProvider;

@Component
public class JwtTokenProviderAdapter implements TokenProvider {

    private final JwtEncoder jwtEncoder;

    // Tempo de expiração do token (ex: 24 horas)
    private static final long EXPIRATION_HOURS = 24;

    public JwtTokenProviderAdapter(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(EXPIRATION_HOURS, ChronoUnit.HOURS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("tagarelas-api")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .build();

        // O cabeçalho usa HS256, correspondente ao algoritmo HmacSHA256 configurado no SpringSecurity
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}