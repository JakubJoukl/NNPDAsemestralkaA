package com.example.semestralkaa.controllers;

import com.example.semestralkaa.dto.ChangePasswordDto;
import com.example.semestralkaa.entity.ResetToken;
import com.example.semestralkaa.entity.User;
import com.example.semestralkaa.dto.LoginDto;
import com.example.semestralkaa.dto.RegistrationDto;
import com.example.semestralkaa.dto.ResetPasswordDto;
import com.example.semestralkaa.security.JwtService;
import com.example.semestralkaa.services.EmailService;
import com.example.semestralkaa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;


    @PostMapping("/login")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody LoginDto authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return ResponseEntity.status(200).body(jwtService.generateToken(authRequest.getUsername()));
        } else {
            return ResponseEntity.status(403).body("User not created");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationDto registrationRequest){
        boolean userCreated = userService.registerUser(registrationRequest);
        if(!userCreated) return ResponseEntity.status(500).body("Failed to create user");
        else return ResponseEntity.status(201).body("User created");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody LoginDto authRequest) throws MessagingException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Bad login");
        }
        User user = userService.getUserByUsername(authRequest.getUsername());
        emailService.sendResetTokenEmail(user);
        return ResponseEntity.status(200).body("Reset email send");
    }

    @PostMapping("/newPassword")
    public ResponseEntity<String> newPassword(@RequestBody ResetPasswordDto resetPasswordRequest){
        ResetToken resetToken = userService.getResetTokenByValue(resetPasswordRequest.getToken());

        if(resetToken == null) return ResponseEntity.status(400).body("Reset token not found");

        boolean resetTokenIsValid = resetToken.isValid() && LocalDateTime.now().isBefore(resetToken.getValidTo());
        if(resetTokenIsValid){
            User user = resetToken.getUser();
            user.setPassword(resetPasswordRequest.getPassword());
            userService.saveUser(user);
        }
        resetToken.setValid(false);
        userService.saveResetToken(resetToken);

        if(resetTokenIsValid) return ResponseEntity.status(200).body("Password reset");
        else return ResponseEntity.status(400).body("Reset token was already used or expired");
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(ChangePasswordDto changePasswordDto){
        if(changePasswordDto.getJwtToken() == null || new Date().after(jwtService.extractExpiration(changePasswordDto.getJwtToken()))){
            return ResponseEntity.status(403).body("Unauthorized");
        }
        String username = jwtService.extractUsername(changePasswordDto.getJwtToken());
        userService.changePassword(changePasswordDto, username);

        return ResponseEntity.status(200).body("Unauthorized");
    }

    //TODO samostatne controllery pro merici zarizeni a senzor
}
