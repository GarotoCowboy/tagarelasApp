package br.com.tagarelas.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tagarelas.services.AuthService;
import br.com.tagarelas.services.input.LoginInput;
import br.com.tagarelas.services.output.AuthOutput;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthOutput> login(@RequestBody LoginInput loginInput) {
        AuthOutput authOutput = authService.login(loginInput);
        return ResponseEntity.ok(authOutput);
    }
}