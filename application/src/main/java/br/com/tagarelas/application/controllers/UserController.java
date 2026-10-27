package br.com.tagarelas.application.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rethinkdb.gen.ast.Uuid;

import br.com.tagarelas.core.enums.OtpDeliveryMethod;
import br.com.tagarelas.core.enums.UserStatusPresence;
import br.com.tagarelas.services.UserService;
import br.com.tagarelas.services.input.ChangePasswordInput;
import br.com.tagarelas.services.input.EditUserInput;
import br.com.tagarelas.services.input.RegisterUserInput;
import br.com.tagarelas.services.output.UserOutput;
import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOutput> findById(@PathVariable UUID id) {

        UserOutput findedUser = userService.findById(id);

        return ResponseEntity.ok(findedUser);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserOutput> findByUsername(@PathVariable String username) {
        UserOutput findedUser = userService.findByUsername(username);

        return ResponseEntity.ok(findedUser);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserOutput> findByEmail(@PathVariable String email) {
        UserOutput findedUser = userService.findByEmail(email);

        return ResponseEntity.ok(findedUser);
    }

    @PostMapping("/register")
    public ResponseEntity<UserOutput> registerUser(@RequestBody RegisterUserInput registerUserInput) {

        UserOutput userOutput = userService.register(registerUserInput);

        return ResponseEntity.status(HttpStatus.CREATED).body(userOutput);
    }

    @PostMapping("/activate/{id}")
    public ResponseEntity<UserOutput> activateAccount(
            @PathVariable UUID id,
            @RequestParam String otpCode) {
        UserOutput userOutput = userService.activateUser(id, otpCode);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userOutput);
    }

    @PostMapping("/resend/code/{id}")
    public ResponseEntity<Void> resendOtpCode(@PathVariable UUID id,
                                              @RequestBody OtpDeliveryMethod otpDeliveryMethod) {
        userService.resendOtp(id, otpDeliveryMethod);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable UUID id){
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

        @PutMapping("/ban/{id}")
    public ResponseEntity<Void> banUser (@PathVariable UUID id){
        userService.banUser(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserOutput> edit(@PathVariable  UUID id, @RequestBody EditUserInput input){
        UserOutput userOutput = userService.edit(id, input);

        return ResponseEntity.ok(userOutput);
    }

    @PutMapping("/changePassword/{id}")
    public ResponseEntity<Void> changePassword(@PathVariable UUID id, @RequestBody ChangePasswordInput input) {
        
        userService.changePassword(id, input);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("updatePresence/{id}")
    public ResponseEntity<UserOutput> updatePresence(@PathVariable UUID id, @RequestParam UserStatusPresence userStatusPresence) {
        
       UserOutput userOutput = userService.updatePresence(id, userStatusPresence);
        
        return ResponseEntity.ok(userOutput);
    }

}
