package br.com.tagarelas.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.User;
import br.com.tagarelas.core.exceptions.DomainValidationException;
import br.com.tagarelas.core.exceptions.ErrorCode;
import br.com.tagarelas.core.exceptions.UserException;
import br.com.tagarelas.services.events.UserOtpCodeEvent;
import br.com.tagarelas.services.events.UserRegisteredEvent;
import br.com.tagarelas.services.input.EditUserInput;
import br.com.tagarelas.services.input.RegisterUserInput;
import br.com.tagarelas.services.output.UserOutput;
import br.com.tagarelas.services.ports.EventPublisher;
import br.com.tagarelas.services.ports.OtpRepository;
import br.com.tagarelas.services.ports.PasswordEncoder;
import br.com.tagarelas.services.ports.UserRepository;

public class UserService {
    private final long EXPIRATION_TIME_IN_SECONDS = 1000;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;
    private final OtpRepository otpRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EventPublisher eventPublisher,
            OtpRepository otpRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.otpRepository = otpRepository;
    }

    public UserOutput register(RegisterUserInput input) {
        if (userRepository.existsByEmail(input.email()))
            throw new DomainValidationException("Email already in use");

        if (userRepository.existsByUsername(input.username()))
            throw new DomainValidationException("Username already in use");

        if (userRepository.existsByPhoneNumber(input.phoneNumber()))
            throw new DomainValidationException("Phone number already in use");

        User newUser = new User(
                input.username(),
                passwordEncoder.encode(input.rawPassword()),
                input.fullName(),
                input.birthday(),
                input.email(),
                input.phoneNumber());

        User savedUser = userRepository.save(newUser);

        String otpCode = generateOTPCode(6);

        otpRepository.save(savedUser.getId(), otpCode, EXPIRATION_TIME_IN_SECONDS);

        eventPublisher.publish("user-registered-topic", new UserRegisteredEvent(
                savedUser.getId(), savedUser.getEmail(), savedUser.getPhoneNumber(), LocalDateTime.now()));

        switch (input.otpDeliveryMethod()) {
            case EMAIL -> eventPublisher.publish("user-otpCode-email-topic", new UserOtpCodeEvent(
                    savedUser.getId(), otpCode, savedUser.getEmail(), null));

            case SMS -> eventPublisher.publish("user-otpCode-phoneNumber-topic", new UserOtpCodeEvent(
                    savedUser.getId(), otpCode, null, savedUser.getPhoneNumber()));

            default -> throw new IllegalArgumentException("otpCodePreference must be email or phoneNumber");
        }

        UserOutput output = toOutput(savedUser);

        return output;
    }

    public UserOutput findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new DomainValidationException("Username cannot be null or blank");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_USERNAME,
                        "User not found with this username: " + username));

        UserOutput output = toOutput(user);

        return output;
    }

    public UserOutput findById(UUID id) {
        if (id == null) {
            throw new DomainValidationException("Id cannot be null or blank");
        }
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserException(ErrorCode.USER_NOT_FOUND_ID, "User not found with this id: " + id));

        UserOutput output = toOutput(user);

        return output;
    }

    public UserOutput findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DomainValidationException("Email cannot be null or blank");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_EMAIL,
                        "User not found with this email: " + email));

        UserOutput output = toOutput(user);

        return output;

    }

    public void delete(UUID id) {
        if (id == null) {
            throw new DomainValidationException("Id cannot be null or blank");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID, "User not found with id: " + id));
        user.deleteUser();

        userRepository.save(user);
    }

    public void banUser(UUID id) {
        if (id == null) {
            throw new DomainValidationException("Id cannot be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID, "User not found with id: " + id));

        user.banUser();

        userRepository.save(user);
    }

    public UserOutput edit(UUID id, EditUserInput input) {
        if(id == null){
            throw new DomainValidationException("Id cannot be null");
        }

        if(input == null){
            throw new DomainValidationException("Input cannot be null");
        }
        boolean hasEmail = input.email() != null && !input.email().isBlank();
        boolean hasPhoneNumber = input.phoneNumber() != null && !input.phoneNumber().isBlank();
        boolean hasPhotoUrl = input.photoURL() != null && !input.photoURL().isBlank();

        if (!hasEmail && !hasPhoneNumber && !hasPhotoUrl) {
            throw new DomainValidationException("At least one field must be provided to edit");
        }
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserException(ErrorCode.USER_NOT_FOUND_ID, "User not found with this id: " + id));

        if (hasEmail) {
            user.setEmail(input.email());
        }
        if (hasPhoneNumber) {
            user.setPhoneNumber(input.phoneNumber());
        }
        if (hasPhotoUrl) {
            user.setPhotoURL(input.photoURL());
        }

        User savedUser = userRepository.save(user);

        UserOutput output = toOutput(savedUser);

        return output;
    }

    public String generateOTPCode(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size can be higher than 0");
        }

        StringBuilder sb = new StringBuilder(size);
        SecureRandom secure = new SecureRandom();

        for (int i = 0; i < size; i++) {
            int digit = secure.nextInt(10);// Vinicius seu burro aqui vai gerar de 0 ate 9
            sb.append(digit);
        }
        return sb.toString();
    }

    private UserOutput toOutput(User user) {
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
