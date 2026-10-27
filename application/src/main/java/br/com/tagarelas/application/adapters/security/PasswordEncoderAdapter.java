package br.com.tagarelas.application.adapters.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.tagarelas.services.ports.PasswordEncoder;

@Component 
public class PasswordEncoderAdapter implements PasswordEncoder {

    private final BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(String rawPassword) {

        return bCrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
       return bCrypt.matches(rawPassword, encodedPassword);
    }


}
