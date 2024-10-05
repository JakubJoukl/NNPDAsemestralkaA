package com.example.semestralkaa.services;

import com.example.semestralkaa.dto.AddMeasuringDeviceSensorDto;
import com.example.semestralkaa.dto.MeasuringDeviceDto;
import com.example.semestralkaa.dto.SensorDto;
import com.example.semestralkaa.entity.MeasuringDevice;
import com.example.semestralkaa.entity.Sensor;
import com.example.semestralkaa.entity.User;
import com.example.semestralkaa.repositories.MeasuringDeviceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeasuringDeviceService {
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MeasuringDeviceRepository measuringDeviceRepository;

    @Autowired
    private ModelMapper modelMapper;

    public void saveMeasuringDevice(MeasuringDevice measuringDevice){
        measuringDeviceRepository.save(measuringDevice);
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

    public boolean updateUserDevice(String deviceName, List<Sensor> sensors, User user) {
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

    public boolean deleteUserMeasuringDevice(String deviceName, User user) {
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
