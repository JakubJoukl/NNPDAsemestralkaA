package com.example.semestralkaa.controllers;

import com.example.semestralkaa.dto.AddDeviceDto;
import com.example.semestralkaa.dto.DeleteDeviceDto;
import com.example.semestralkaa.dto.UpdateDeviceDto;
import com.example.semestralkaa.entity.MeasuringDevice;
import com.example.semestralkaa.entity.Sensor;
import com.example.semestralkaa.entity.User;
import com.example.semestralkaa.services.MeasuringDeviceService;
import com.example.semestralkaa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/device")
public class MeasuringDeviceController {

    @Autowired
    private UserService userService;

    @Autowired
    private MeasuringDeviceService measuringDeviceService;

    @PostMapping("/addDevice")
    public ResponseEntity<String> addDevice(@RequestBody AddDeviceDto addDeviceDto){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");

        List<Sensor> sensors = measuringDeviceService.convertAddMeasuringDeviceSensorDtoToEntityList(addDeviceDto.getSensors());
        boolean deviceAdded = measuringDeviceService.addMeasuringDevice(addDeviceDto.getDeviceName(), sensors, user);

        if(deviceAdded) return ResponseEntity.status(200).body("Device registered");
        else return ResponseEntity.status(500).body("Failed to add device");
    }

    @DeleteMapping("/deleteDevice")
    public ResponseEntity<String> deleteDevice(@RequestBody DeleteDeviceDto deleteDeviceDto){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");
        boolean deviceAdded = measuringDeviceService.deleteUserMeasuringDevice(deleteDeviceDto.getDeviceName(), user);

        if(deviceAdded) return ResponseEntity.status(200).body("Device deleted");
        else return ResponseEntity.status(500).body("Failed to delete device");
    }

    @GetMapping("/getDevice/{deviceName}")
    public ResponseEntity<?> getDevice(@PathVariable("deviceName") String deviceName){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");

        MeasuringDevice measuringDevice = measuringDeviceService.getDevice(deviceName, user);
        if(measuringDevice == null) return ResponseEntity.status(500).body("Device not found");
        return ResponseEntity.status(200).body(measuringDeviceService.convertToDto(measuringDevice));
    }

    @PutMapping("/updateDevice")
    public ResponseEntity<String> updateDevice(@RequestBody UpdateDeviceDto updateDeviceDto){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");

        List<Sensor> sensors = measuringDeviceService.convertAddMeasuringDeviceSensorDtoToEntityList(updateDeviceDto.getSensors());
        boolean deviceUpdated = measuringDeviceService.updateUserDevice(updateDeviceDto.getDeviceName(), sensors, user);

        if(deviceUpdated) return ResponseEntity.status(200).body("Device updated");
        else return ResponseEntity.status(500).body("Failed to update device");
    }
}
