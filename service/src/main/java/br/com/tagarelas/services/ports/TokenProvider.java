package br.com.tagarelas.services.ports;

import br.com.tagarelas.core.User;

public interface TokenProvider {

    String generateToken(User user);
}

