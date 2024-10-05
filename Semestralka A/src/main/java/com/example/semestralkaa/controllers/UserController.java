package com.example.semestralkaa.controllers;

import com.example.semestralkaa.dto.*;
import com.example.semestralkaa.entity.MeasuringDevice;
import com.example.semestralkaa.entity.ResetToken;
import com.example.semestralkaa.entity.Sensor;
import com.example.semestralkaa.entity.User;
import com.example.semestralkaa.security.JwtService;
import com.example.semestralkaa.services.EmailService;
import com.example.semestralkaa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;

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
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto authRequest) throws MessagingException {
        User user = userService.getUserByUsername(authRequest.getUsername());
        if(user == null) return ResponseEntity.status(404).body("User not found");
        emailService.sendResetTokenEmail(user);
        return ResponseEntity.status(200).body("Reset email send");
    }

    @PostMapping("/newPassword")
    public ResponseEntity<String> newPassword(@RequestBody NewPasswordDto resetPasswordRequest){
        ResetToken resetToken = userService.getResetTokenByValue(resetPasswordRequest.getToken());

        if(resetToken == null) return ResponseEntity.status(400).body("Reset token not found");

        boolean resetTokenIsValid = resetToken.isValid() && LocalDateTime.now().isBefore(resetToken.getValidTo());
        if(resetTokenIsValid){
            User user = resetToken.getUser();
            userService.changePassword(resetPasswordRequest.getPassword(), user);
            userService.saveUser(user);
        }
        resetToken.setValid(false);
        userService.saveResetToken(resetToken);

        if(resetTokenIsValid) return ResponseEntity.status(200).body("Password reset");
        else return ResponseEntity.status(400).body("Reset token was already used or expired");
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        User user = userService.getUserFromContext();

        if(user == null) return ResponseEntity.status(404).body("User not found");
        String oldPassword = changePasswordDto.getOldPassword();
        if(userService.checkUserPassword(oldPassword, user)) return ResponseEntity.status(400).body("Old password was wrong");

        String newPassword = changePasswordDto.getNewPassword();
        userService.changePassword(newPassword, user);

        return ResponseEntity.status(200).body("Password changed");
    }

    @PostMapping("/addDevice")
    public ResponseEntity<String> addDevice(@RequestBody AddDeviceDto addDeviceDto){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");

        List<Sensor> sensors = userService.convertAddMeasuringDeviceSensorDtoToEntityList(addDeviceDto.getSensors());
        boolean deviceAdded = userService.addMeasuringDevice(addDeviceDto.getDeviceName(), sensors, user);

        if(deviceAdded) return ResponseEntity.status(200).body("Device registered");
        else return ResponseEntity.status(500).body("Failed to add device");
    }

    @DeleteMapping("/deleteDevice")
    public ResponseEntity<String> deleteDevice(@RequestBody DeleteDeviceDto deleteDeviceDto){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");
        boolean deviceAdded = userService.deleteMeasuringDevice(deleteDeviceDto.getDeviceName(), user);

        if(deviceAdded) return ResponseEntity.status(200).body("Device deleted");
        else return ResponseEntity.status(500).body("Failed to delete device");
    }

    @GetMapping("/getDevice/{deviceName}")
    public ResponseEntity<?> getDevice(@PathVariable("deviceName") String deviceName){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");

        MeasuringDevice measuringDevice = userService.getDevice(deviceName, user);
        if(measuringDevice == null) return ResponseEntity.status(500).body("Device not found");
        return ResponseEntity.status(200).body(userService.convertToDto(measuringDevice));
    }

    @PutMapping("/updateDevice")
    public ResponseEntity<String> updateDevice(@RequestBody UpdateDeviceDto updateDeviceDto){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");

        List<Sensor> sensors = userService.convertAddMeasuringDeviceSensorDtoToEntityList(updateDeviceDto.getSensors());
        boolean deviceUpdated = userService.updateDevice(updateDeviceDto.getDeviceName(), sensors, user);

        if(deviceUpdated) return ResponseEntity.status(200).body("Device updated");
        else return ResponseEntity.status(500).body("Failed to update device");
    }
}
