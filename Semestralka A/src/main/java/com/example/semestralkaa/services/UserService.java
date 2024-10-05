package com.example.semestralkaa.services;


import com.example.semestralkaa.dto.*;
import com.example.semestralkaa.entity.MeasuringDevice;
import com.example.semestralkaa.entity.ResetToken;
import com.example.semestralkaa.entity.Sensor;
import com.example.semestralkaa.entity.User;
import com.example.semestralkaa.repositories.MeasuringDeviceRepository;
import com.example.semestralkaa.repositories.ResetTokenRepository;
import com.example.semestralkaa.repositories.UserRepository;
import com.example.semestralkaa.security.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeasuringDeviceRepository measuringDeviceRepository;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtService jwtService;

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

    public void saveMeasuringDevice(MeasuringDevice measuringDevice){
        measuringDeviceRepository.save(measuringDevice);
    }

    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public MeasuringDeviceDto convertToDto(MeasuringDevice measuringDevice) {
        return modelMapper.map(measuringDevice, MeasuringDeviceDto.class);
    }

    public MeasuringDevice convertToEntity(MeasuringDeviceDto measuringDeviceDto) {
        return modelMapper.map(measuringDeviceDto, MeasuringDevice.class);
    }

    public SensorDto convertToDto(Sensor sensor) {
        return modelMapper.map(sensor, SensorDto.class);
    }

    public List<SensorDto> convertToDtoList(List<Sensor> sensors) {
        return sensors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    public Sensor convertToEntity(SensorDto sensorDto) {
        return modelMapper.map(sensorDto, Sensor.class);
    }

    public Sensor convertAddMeasuringDeviceSensorDtoToEntity(AddMeasuringDeviceSensorDto sensorDto){
        return new Sensor(sensorDto.getSensorName());
    }

    public List<Sensor> convertToEntityList(List<SensorDto> sensorDtos) {
        return sensorDtos.stream()
                .map(sensorDto -> new Sensor(sensorDto.getSensorName()))
                .collect(Collectors.toList());
    }

    public List<Sensor> convertAddMeasuringDeviceSensorDtoToEntityList(List<AddMeasuringDeviceSensorDto> sensorDtos) {
        return sensorDtos.stream()
                .map(this::convertAddMeasuringDeviceSensorDtoToEntity)
                .collect(Collectors.toList());
    }

    public boolean addMeasuringDevice(String deviceName, List<Sensor> sensors, User user) {
        if(getUserMeasuringDeviceByName(deviceName, user) != null){
            return false;
        } else {
            MeasuringDevice measuringDevice = new MeasuringDevice(deviceName, sensors, user);
            sensors.forEach(sensor -> sensor.setMeasuringDevice(measuringDevice));
            user.getMeasuringDevices().add(measuringDevice);
            saveMeasuringDevice(measuringDevice);
            return true;
        }
    }

    public boolean updateDevice(String deviceName, List<Sensor> sensors, User user) {
        MeasuringDevice measuringDevice = getUserMeasuringDeviceByName(deviceName, user);
        if(measuringDevice != null){
            measuringDevice.setSensors(sensors);
            sensors.forEach(sensor -> sensor.setMeasuringDevice(measuringDevice));
            measuringDevice.setDeviceName(deviceName);
            saveMeasuringDevice(measuringDevice);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteMeasuringDevice(String deviceName, User user) {
        MeasuringDevice measuringDevice = getUserMeasuringDeviceByName(deviceName, user);
        if(measuringDevice == null) return false;
        else {
            user.getMeasuringDevices().remove(measuringDevice);
            measuringDeviceRepository.delete(measuringDevice);
            return true;
        }
    }

    private MeasuringDevice getUserMeasuringDeviceByName(String deviceName, User user) {
        return user.getMeasuringDevices().stream().filter(measuringDevice1 -> measuringDevice1.getDeviceName().equals(deviceName)).findFirst().orElse(null);
    }

    public MeasuringDevice getDevice(String deviceName, User user) {
        return user.getMeasuringDevices().stream().filter(measuringDevice -> measuringDevice.getDeviceName().equals(deviceName)).findFirst().orElse(null);
    }
}
