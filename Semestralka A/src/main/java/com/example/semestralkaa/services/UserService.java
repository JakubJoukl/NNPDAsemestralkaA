package com.example.semestralkaa.services;


import com.example.semestralkaa.dto.*;
import com.example.semestralkaa.entity.ResetToken;
import com.example.semestralkaa.entity.User;
import com.example.semestralkaa.repositories.MeasuringDeviceRepository;
import com.example.semestralkaa.repositories.ResetTokenRepository;
import com.example.semestralkaa.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username).orElse(null);
    }

    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username).orElse(null);
    }

    public User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getUserByUsername(authentication.getName());
        return user;
    }

    public ResetToken getResetTokenByValue(String resetTokenValue){
        return resetTokenRepository.getResetTokenByToken(resetTokenValue).orElseThrow(() -> new RuntimeException("Nebyl nalezen token"));
    }

    public void deactivateUserResetTokens(User user){
        user.getResetTokens().forEach(resetToken -> {
            resetToken.setValid(false);
            resetTokenRepository.save(resetToken);
        });
    }

    public boolean registerUser(RegistrationDto registrationRequest) {
        boolean alreadyExists = userRepository.getUserByUsername(registrationRequest.getUsername()).isPresent();
        if(alreadyExists) return false;
        else return userRepository.save(new User(null, registrationRequest.getUsername(), encryptPassword(registrationRequest.getPassword()), registrationRequest.getEmail(), null, null)).getUserId() != null;
    }

    public void changePassword(String newPassword, User user) {
        user.setPassword(encryptPassword(newPassword));
        saveUser(user);
    }

    public String encryptPassword(String password){
        return passwordEncoder.encode(password);
    }

    public boolean checkUserPassword(String password, User user){
        return passwordEncoder.matches(password, user.getPassword());
    }

    public void saveResetToken(ResetToken resetToken){
        resetTokenRepository.save(resetToken);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }


    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
