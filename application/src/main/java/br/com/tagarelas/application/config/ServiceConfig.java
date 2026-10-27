package br.com.tagarelas.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.tagarelas.services.AuthService;
import br.com.tagarelas.services.UserService;
import br.com.tagarelas.services.ports.EventPublisher;
import br.com.tagarelas.services.ports.OtpRepository;
import br.com.tagarelas.services.ports.PasswordEncoder;
import br.com.tagarelas.services.ports.TokenProvider;
import br.com.tagarelas.services.ports.UserRepository;

@Configuration
public class ServiceConfig {

    @Bean 
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder,EventPublisher eventPublisher, OtpRepository otpRepository){
        return new UserService(userRepository, passwordEncoder, eventPublisher, otpRepository);
    }

    @Bean
    public AuthService authService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider){
        return new AuthService(userRepository, passwordEncoder, tokenProvider);
    }
}
