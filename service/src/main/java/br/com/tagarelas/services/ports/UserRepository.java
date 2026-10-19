package br.com.tagarelas.services.ports;

import java.util.Optional;
import java.util.UUID;

import br.com.tagarelas.core.User;
import br.com.tagarelas.services.input.RegisterUserInput;
public interface UserRepository {


    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    Optional<User> edit(UUID id,RegisterUserInput registerUserInput);
    
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);
}
