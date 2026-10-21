package br.com.tagarelas.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.tagarelas.core.User;
import br.com.tagarelas.core.enums.OtpDeliveryMethod;
import br.com.tagarelas.core.enums.UserStatus;
import br.com.tagarelas.core.enums.UserStatusPresence;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;
import br.com.tagarelas.core.exceptions.ErrorCode;
import br.com.tagarelas.core.exceptions.UserException;
import br.com.tagarelas.services.events.UserOtpCodeEvent;
import br.com.tagarelas.services.events.UserRegisteredEvent;
import br.com.tagarelas.services.input.ChangePasswordInput;
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

    public UserOutput activateUser(UUID userId, String otpCode) {
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        if (otpCode == null || otpCode.isBlank()) {
            throw new DomainValidationException("OTP code cannot be null or blank");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID, "User not found with id: " + userId));

        String storedOtp = otpRepository.get(userId)
                .orElseThrow(() -> new UserException(ErrorCode.OTP_EXPIRED_OR_NOT_FOUND,
                        "OTP code expired or not found for user: " + userId));

        if (!storedOtp.equals(otpCode)) {
            throw new UserException(ErrorCode.INVALID_OTP_CODE, "Invalid OTP code");
        }

        user.activateUser();

        User savedUser = userRepository.save(user);
        otpRepository.delete(userId);

        return toOutput(savedUser);
    }

    public void resendOtp(UUID userId, OtpDeliveryMethod otpDeliveryMethod) {
        if (userId == null) {
            throw new DomainValidationException("User ID cannot be null");
        }
        if (otpDeliveryMethod == null) {
            throw new DomainValidationException("OtpDeliveryMethod cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID, "User not found with id: " + userId));

        if (user.getUserStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new BusinessRuleViolationException("Only pending accounts can request an OTP code");
        }

        String otpCode = generateOTPCode(6);
        otpRepository.save(user.getId(), otpCode, EXPIRATION_TIME_IN_SECONDS);

        switch (otpDeliveryMethod) {
            case EMAIL -> eventPublisher.publish("user-otpCode-email-topic", new UserOtpCodeEvent(
                    user.getId(), otpCode, user.getEmail(), null));

            case SMS -> eventPublisher.publish("user-otpCode-phoneNumber-topic", new UserOtpCodeEvent(
                    user.getId(), otpCode, null, user.getPhoneNumber()));

            default -> throw new IllegalArgumentException("otpCodePreference must be email or phoneNumber");
        }
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
            if (!input.email().equals(user.getEmail()) && userRepository.existsByEmail(input.email())) {
                throw new DomainValidationException("Email already in use");
            }
            user.setEmail(input.email());
        }
        if (hasPhoneNumber) {
            if (!input.phoneNumber().equals(user.getPhoneNumber())
                    && userRepository.existsByPhoneNumber(input.phoneNumber())) {
                throw new DomainValidationException("Phone number already in use");
            }
            user.setPhoneNumber(input.phoneNumber());
        }
        if (hasPhotoUrl) {
            user.setPhotoURL(input.photoURL());
        }

        User savedUser = userRepository.save(user);

        UserOutput output = toOutput(savedUser);

        return output;
    }

    public void changePassword(UUID id, ChangePasswordInput input) {
        if (id == null) {
            throw new DomainValidationException("Id cannot be null");
        }
        if (input == null) {
            throw new DomainValidationException("Input cannot be null");
        }
        if (input.currentPassword() == null || input.currentPassword().isBlank()) {
            throw new DomainValidationException("Current password cannot be null or blank");
        }
        if (input.newPassword() == null || input.newPassword().isBlank()) {
            throw new DomainValidationException("New password cannot be null or blank");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID, "User not found with this id: " + id));

        if (!passwordEncoder.matches(input.currentPassword(), user.getPassword())) {
            throw new UserException(ErrorCode.USER_INVALID_CURRENT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(input.newPassword()));
        userRepository.save(user);
    }

    public UserOutput updatePresence(UUID id, UserStatusPresence presence) {
        if (id == null) {
            throw new DomainValidationException("Id cannot be null");
        }
        if (presence == null) {
            throw new DomainValidationException("Presence status cannot be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND_ID, "User not found with this id: " + id));

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only active accounts can update presence");
        }

        user.setUserStatusPresence(presence);
        User savedUser = userRepository.save(user);
        return toOutput(savedUser);
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
