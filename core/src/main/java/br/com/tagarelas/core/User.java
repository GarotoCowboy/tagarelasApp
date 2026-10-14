package br.com.tagarelas.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;


import br.com.tagarelas.core.enums.UserStatus;
import br.com.tagarelas.core.enums.UserStatusPresence;

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
        String phoneNumber) throws Exception {
    this.username = username;
    this.password = passwordHash;
    this.fullName = fullName;
    this.birthday = birthday;
    this.photoURL = null;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.userStatusPresence = UserStatusPresence.OFFLINE;
    this.userStatus =  UserStatus.PENDING_VERIFICATION;
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
        int devicesConnected,LocalDateTime createdAt,LocalDateTime updatedAt, LocalDateTime deletedAt) {
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


private void validatePassword(String password)throws Exception{

    if(password == null){
        throw new Exception("Password cannot be null");
    }
}

private void validateUsername(String username) throws Exception{
if(username == null || username.isBlank() ){
    throw new Exception("Username cannot be null or Blank");
    }
}

private void validateFullName(String FullName) throws Exception{
    if(FullName == null || FullName.isBlank() ){
    throw new Exception("Full name cannot be null or Blank");
    }
}

private void validateEmail(String email) throws Exception{

if(email == null || email.isBlank()){
    throw new Exception("E-mail cannot be null or Blank");
}

if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
    throw new IllegalArgumentException("Invalid E-mail");
}
}


private void validatePhoneNumber(String phoneNumber) throws Exception{
    
    if(phoneNumber == null || phoneNumber.isBlank()){
        throw new Exception("Phone number cannot be null or Blank");
    }  
    boolean isAValidPhoneNumber = phoneNumber.chars()
                                     .allMatch(Character::isDigit);    
                 
   if(isAValidPhoneNumber == false){
    throw new IllegalArgumentException("Invalid phone number");
   }
                                     
}

private void validateBirthDay(LocalDate birthDay) throws Exception{
        if(birthDay == null){
        throw new Exception("BirthDay cannot be Null");
    }

    int age = Period.between(birthDay, LocalDate.now()).getYears();

    if(age < 18){
        throw new Exception("User cannot be under than 18 years");
    }
}


public void activateUser(){
    if(this.userStatus != UserStatus.PENDING_VERIFICATION){
        throw new IllegalStateException("Only pendings accounts can be activated");
    }
    this.userStatus = UserStatus.ACTIVE;
    this.updatedAt = LocalDateTime.now();
}

public void deleteUser(){
    if(this.userStatus != UserStatus.ACTIVE){
        throw new IllegalStateException("Only acive accounts can be desactivated");
    }
    this.userStatus = UserStatus.DELETED;
    this.updatedAt = LocalDateTime.now();
    this.deletedAt = LocalDateTime.now();
}

public void banUser(){
    if(this.userStatus != UserStatus.ACTIVE){
        throw new IllegalStateException("Only acive accounts can be desactivated");
    }
    this.userStatus = UserStatus.BANNED;
    this.updatedAt = LocalDateTime.now();
}

public UUID getId() {
    return id;
}



public String getUsername() {
    return username;
}


public void setUsername(String username) throws Exception {
    this.updatedAt = LocalDateTime.now();
    validateUsername(username);
    this.username = username;
}


public String getPassword() {
    return password;
}


public void setPassword(String password) throws Exception {
    this.updatedAt = LocalDateTime.now();
    validatePassword(password);
    this.password = password;
}


public String getFullName() {
    return fullName;
}


public void setFullName(String fullName) throws Exception {
    this.updatedAt = LocalDateTime.now();
    validateFullName(fullName);
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


public void setEmail(String email) throws Exception {
    this.updatedAt = LocalDateTime.now();
    validateEmail(email);
    this.email = email;
}


public String getPhoneNumber() {
    return phoneNumber;
}


public void setPhoneNumber(String phoneNumber) throws Exception {
    this.updatedAt = LocalDateTime.now();
    validatePhoneNumber(phoneNumber);
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
