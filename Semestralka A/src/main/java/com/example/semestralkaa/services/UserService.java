package com.example.semestralkaa.services;


import com.example.semestralkaa.dto.ChangePasswordDto;
import com.example.semestralkaa.dto.UserDto;
import com.example.semestralkaa.entity.ResetToken;
import com.example.semestralkaa.entity.User;
import com.example.semestralkaa.repositories.ResetTokenRepository;
import com.example.semestralkaa.dto.RegistrationDto;
import com.example.semestralkaa.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

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
        else return userRepository.save(new User(null, registrationRequest.getUsername(), new BCryptPasswordEncoder().encode(registrationRequest.getPassword()), registrationRequest.getEmail(), null)).getUserId() != null;
    }

    public void changePassword(ChangePasswordDto changePasswordDto, String username) {
        User user = getUserByUsername(username);
        user.setPassword(changePasswordDto.getNewPassword());
        saveUser(user);
    }

    public void saveResetToken(ResetToken resetToken){
        resetTokenRepository.save(resetToken);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    //TODO zatim mam manualne, vyuziju toto?
    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
