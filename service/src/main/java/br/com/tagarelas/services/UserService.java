package br.com.tagarelas.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import br.com.tagarelas.core.User;
import br.com.tagarelas.core.exceptions.DomainValidationException;
import br.com.tagarelas.services.events.UserOtpCodeEvent;
import br.com.tagarelas.services.events.UserRegisteredEvent;
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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EventPublisher eventPublisher,OtpRepository otpRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.otpRepository = otpRepository;
    }

    public UserOutput register(RegisterUserInput input) {
        if(userRepository.existsByEmail(input.email()))
            throw new DomainValidationException("Email already in use");

        if(userRepository.existsByUsername(input.username()))
            throw new DomainValidationException("Username already in use");

        if(userRepository.existsByPhoneNumber(input.phoneNumber()))
            throw new DomainValidationException("Phone number already in use");
        

        User newUser = new User(
            input.username(),
            passwordEncoder.encode(input.rawPassword()),
            input.fullName(),
             input.birthday(),
             input.email(),
             input.phoneNumber()
        );
        
        User savedUser = userRepository.save(newUser);

        String otpCode = generateOTPCode(6);

        otpRepository.save(savedUser.getId(), otpCode, EXPIRATION_TIME_IN_SECONDS);


                eventPublisher.publish("user-registered-topic", new UserRegisteredEvent(
                        savedUser.getId(),savedUser.getEmail(), savedUser.getPhoneNumber(), LocalDateTime.now()));

                    
                        switch (input.otpDeliveryMethod()) {
                            case EMAIL -> eventPublisher.publish("user-otpCode-email-topic", new UserOtpCodeEvent(
                                        savedUser.getId(),otpCode, savedUser.getEmail(), null));

                            case SMS -> eventPublisher.publish("user-otpCode-phoneNumber-topic", new UserOtpCodeEvent(
                                        savedUser.getId(),otpCode,null,savedUser.getPhoneNumber()
                                    ));
                        
                            default -> throw new IllegalArgumentException("otpCodePreference must be email or phoneNumber");
                        }

        UserOutput output = new UserOutput(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getFullName(),
            savedUser.getEmail(),
            savedUser.getPhoneNumber(),
            savedUser.getUserStatus(),
            savedUser.getCreatedAt()
        );
        return output;
    }

    


    public String generateOTPCode(int size){
        if(size <= 0){
            throw new IllegalArgumentException("Size can be higher than 0");
        }

        StringBuilder sb = new StringBuilder(size);
        SecureRandom secure = new SecureRandom();

        for(int i = 0; i < size; i++){
            int digit = secure.nextInt(10);// Vinicius seu burro aqui vai gerar de 0 ate 9
            sb.append(digit);
        }
        return sb.toString();
    }
}
