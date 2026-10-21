package br.com.tagarelas.services;

import java.util.Optional;

import br.com.tagarelas.core.User;
import br.com.tagarelas.core.enums.UserStatus;
import br.com.tagarelas.core.exceptions.AuthException;
import br.com.tagarelas.core.exceptions.DomainValidationException;
import br.com.tagarelas.core.exceptions.ErrorCode;
import br.com.tagarelas.services.input.LoginInput;
import br.com.tagarelas.services.output.AuthOutput;
import br.com.tagarelas.services.output.UserOutput;
import br.com.tagarelas.services.ports.PasswordEncoder;
import br.com.tagarelas.services.ports.TokenProvider;
import br.com.tagarelas.services.ports.UserRepository;

public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthOutput login(LoginInput input) {
        if (input == null) {
            throw new DomainValidationException("Login input cannot be null");
        }
        if (input.identifier() == null || input.identifier().isBlank()) {
            throw new DomainValidationException("Identifier (email or username) cannot be null or blank");
        }
        if (input.rawPassword() == null || input.rawPassword().isBlank()) {
            throw new DomainValidationException("Password cannot be null or blank");
        }

        Optional<User> optionalUser = input.identifier().contains("@")
                ? userRepository.findByEmail(input.identifier())
                : userRepository.findByUsername(input.identifier());

        User user = optionalUser.orElseThrow(
                () -> new AuthException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(input.rawPassword(), user.getPassword())) {
            throw new AuthException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        validateUserStatusForLogin(user);

        String accessToken = tokenProvider.generateToken(user);

        return new AuthOutput(accessToken, toUserOutput(user));
    }

    private void validateUserStatusForLogin(User user) {
        if (user.getUserStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new AuthException(ErrorCode.AUTH_USER_NOT_ACTIVATED);
        }
        if (user.getUserStatus() == UserStatus.BANNED) {
            throw new AuthException(ErrorCode.AUTH_USER_BANNED);
        }
        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new AuthException(ErrorCode.AUTH_USER_DELETED);
        }
    }

    private UserOutput toUserOutput(User user) {
        return new UserOutput(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getBirthday(),
                user.getEmail(),
                user.getPhotoURL(),
                user.getPhoneNumber(),
                user.getUserStatus(),
                user.getCreatedAt());
    }
}

