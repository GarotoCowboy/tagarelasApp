package br.com.tagarelas.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

import br.com.tagarelas.core.enums.UserStatus;
import br.com.tagarelas.core.enums.UserStatusPresence;
import br.com.tagarelas.core.exceptions.BusinessRuleViolationException;
import br.com.tagarelas.core.exceptions.DomainValidationException;

public class User {
    private UUID id;
    private String username;
    private String password;
    private String fullName;
    private LocalDate birthday;
    private String photoURL;
    private String email;
    private String phoneNumber;
    private UserStatusPresence userStatusPresence;
    private UserStatus userStatus;
    private int devicesConnected;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public User() {
    }

    public User(String username, String passwordHash, String fullName, LocalDate birthday, String email,
            String phoneNumber) {
        this.username = username;
        this.password = passwordHash;
        this.fullName = fullName;
        this.birthday = birthday;
        this.photoURL = null;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userStatusPresence = UserStatusPresence.OFFLINE;
        this.userStatus = UserStatus.PENDING_VERIFICATION;
        this.devicesConnected = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        validateUsername(username);
        validateFullName(fullName);
        validateBirthDay(birthday);
        validatePhoneNumber(phoneNumber);
        validateEmail(email);
        validatePassword(passwordHash);
    }

    public User(UUID id, String username, String password, String fullName, LocalDate birthday, String photoURL,
            String email, String phoneNumber, UserStatusPresence userStatusPresence, UserStatus userStatus,
            int devicesConnected, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.birthday = birthday;
        this.photoURL = photoURL;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userStatusPresence = userStatusPresence;
        this.userStatus = userStatus;
        this.devicesConnected = devicesConnected;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new DomainValidationException("Password hash cannot be null or blank");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new DomainValidationException("Username cannot be null or blank");
        }
    }

    private void validateFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new DomainValidationException("Full name cannot be null or blank");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DomainValidationException("E-mail cannot be null or blank");
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new DomainValidationException("Invalid E-mail format");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new DomainValidationException("Phone number cannot be null or blank");
        }
        boolean isAValidPhoneNumber = phoneNumber.chars().allMatch(Character::isDigit);

        if (!isAValidPhoneNumber) {
            throw new DomainValidationException("Invalid phone number, must contain only digits");
        }
    }

    private void validateBirthDay(LocalDate birthDay) {
        if (birthDay == null) {
            throw new DomainValidationException("BirthDay cannot be null");
        }

        int age = Period.between(birthDay, LocalDate.now()).getYears();

        if (age < 18) {
            throw new DomainValidationException("User cannot be under 18 years old");
        }
    }

    public void activateUser() {
        if (this.userStatus != UserStatus.PENDING_VERIFICATION) {
            throw new BusinessRuleViolationException("Only pending accounts can be activated");
        }
        this.userStatus = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteUser() {
        if (this.userStatus != UserStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only active accounts can be deactivated");
        }
        this.userStatus = UserStatus.DELETED;
        this.userStatusPresence = UserStatusPresence.OFFLINE;
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = LocalDateTime.now();
    }

    public void banUser() {
        if (this.userStatus != UserStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only active accounts can be banned");
        }
        this.userStatus = UserStatus.BANNED;
        this.userStatusPresence = UserStatusPresence.OFFLINE;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        validateUsername(username);
        this.updatedAt = LocalDateTime.now();
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        validatePassword(password);
        this.updatedAt = LocalDateTime.now();
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        validateFullName(fullName);
        this.updatedAt = LocalDateTime.now();
        this.fullName = fullName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.updatedAt = LocalDateTime.now();
        this.photoURL = photoURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.updatedAt = LocalDateTime.now();
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        this.updatedAt = LocalDateTime.now();
        this.phoneNumber = phoneNumber;
    }

    public UserStatusPresence getUserStatusPresence() {
        return userStatusPresence;
    }

    public void setUserStatusPresence(UserStatusPresence userStatusPresence) {
        this.updatedAt = LocalDateTime.now();
        this.userStatusPresence = userStatusPresence;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public int getDevicesConnected() {
        return devicesConnected;
    }

    public void setDevicesConnected(int devicesConnected) {
        if (devicesConnected < 0) {
            throw new DomainValidationException("Devices connected cannot be negative");
        }
        this.updatedAt = LocalDateTime.now();
        this.devicesConnected = devicesConnected;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}
