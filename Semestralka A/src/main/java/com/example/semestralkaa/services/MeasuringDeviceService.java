package com.example.semestralkaa.services;

import com.example.semestralkaa.dto.measuringDevice.AddMeasuringDeviceSensorDto;
import com.example.semestralkaa.dto.measuringDevice.MeasuringDeviceDto;
import com.example.semestralkaa.dto.sensor.SensorDto;
import com.example.semestralkaa.entity.MeasuringDevice;
import com.example.semestralkaa.entity.Sensor;
import com.example.semestralkaa.entity.User;
import com.example.semestralkaa.repositories.MeasuringDeviceRepository;
import com.example.semestralkaa.repositories.SensorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeasuringDeviceService {
    @Autowired
    private MeasuringDeviceRepository measuringDeviceRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private ModelMapper modelMapper;


    public void saveMeasuringDevice(MeasuringDevice measuringDevice){
        measuringDeviceRepository.save(measuringDevice);
    }

    @Transactional(rollbackFor = Exception.class)
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

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserDevice(String deviceName, String newDeviceName, List<Sensor> sensors, User user) {
        MeasuringDevice measuringDevice = getUserMeasuringDeviceByName(deviceName, user);
        if(measuringDevice != null){
            measuringDevice.getSensors().clear();
            sensorRepository.flush();
            measuringDevice.getSensors().addAll(sensors);
            sensors.forEach(sensor -> sensor.setMeasuringDevice(measuringDevice));
            measuringDevice.setDeviceName(newDeviceName);
            saveMeasuringDevice(measuringDevice);
            return true;
        } else {
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserMeasuringDevice(String deviceName, User user) {
        MeasuringDevice measuringDevice = getUserMeasuringDeviceByName(deviceName, user);
        if(measuringDevice == null) return false;
        else {
            user.getMeasuringDevices().remove(measuringDevice);
            measuringDeviceRepository.delete(measuringDevice);
            return true;
        }
    }

    public MeasuringDevice getUserMeasuringDeviceByName(String deviceName, User user) {
        return user.getMeasuringDevices().stream().filter(measuringDevice1 -> measuringDevice1.getDeviceName().equals(deviceName)).findFirst().orElse(null);
    }

    public MeasuringDevice getDevice(String deviceName, User user) {
        return user.getMeasuringDevices().stream().filter(measuringDevice -> measuringDevice.getDeviceName().equals(deviceName)).findFirst().orElse(null);
    }

    public MeasuringDeviceDto convertToDto(MeasuringDevice measuringDevice) {
        return modelMapper.map(measuringDevice, MeasuringDeviceDto.class);
    }

    public MeasuringDevice convertToEntity(MeasuringDeviceDto measuringDeviceDto) {
        return modelMapper.map(measuringDeviceDto, MeasuringDevice.class);
    }

    public Sensor convertAddMeasuringDeviceSensorDtoToEntity(AddMeasuringDeviceSensorDto sensorDto){
        return new Sensor(sensorDto.getSensorName());
    }

    public List<Sensor> convertAddMeasuringDeviceSensorDtoToEntityList(List<AddMeasuringDeviceSensorDto> sensorDtos) {
        return sensorDtos.stream()
                .map(this::convertAddMeasuringDeviceSensorDtoToEntity)
                .collect(Collectors.toList());
    }
}
