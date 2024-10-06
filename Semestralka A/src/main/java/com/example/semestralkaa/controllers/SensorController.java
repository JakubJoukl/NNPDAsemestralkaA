package com.example.semestralkaa.controllers;

import com.example.semestralkaa.dto.sensor.AddSensorDto;
import com.example.semestralkaa.dto.sensor.UpdateSensorDto;
import com.example.semestralkaa.entity.Sensor;
import com.example.semestralkaa.entity.User;
import com.example.semestralkaa.services.SensorService;
import com.example.semestralkaa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user/device/sensor")
public class SensorController {

    @Autowired
    private UserService userService;

    @Autowired
    private SensorService sensorService;

    @PostMapping("/addSensor")
    public ResponseEntity<String> addSensor(@RequestBody AddSensorDto addSensorDto){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");

        boolean sensorAdded = sensorService.addSensor(addSensorDto.getSensorName(), addSensorDto.getDeviceName(), user);

        if(sensorAdded) return ResponseEntity.status(200).body("Sensor added");
        else return ResponseEntity.status(500).body("Failed to add sensor to device");
    }

    @DeleteMapping("/deleteSensor")
    public ResponseEntity<String> deleteSensor(@RequestBody AddSensorDto deleteMeasuringDeviceDto){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");
        boolean deviceAdded = sensorService.deleteMeasuringDeviceSensor(deleteMeasuringDeviceDto.getSensorName(), deleteMeasuringDeviceDto.getDeviceName(), user);

        if(deviceAdded) return ResponseEntity.status(200).body("Sensor deleted");
        else return ResponseEntity.status(500).body("Failed to delete sensor");
    }

    @GetMapping("/getSensor/{deviceName}/{sensorName}")
    public ResponseEntity<?> getSensor(@PathVariable("sensorName") String sensorName, @PathVariable("deviceName") String deviceName){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");

        Sensor sensor = sensorService.getSensorByName(sensorName, deviceName, user);
        if(sensor == null) return ResponseEntity.status(404).body("Sensor not found");
        return ResponseEntity.status(200).body(sensorService.convertToDto(sensor));
    }

    @PutMapping("/updateSensor")
    public ResponseEntity<String> updateSensor(@RequestBody UpdateSensorDto updateSensorDto){
        User user = userService.getUserFromContext();
        if(user == null) return ResponseEntity.status(404).body("User not found");

        boolean sensorUpdated = sensorService.updateMeasuringDeviceSensor(updateSensorDto.getSensorName(), updateSensorDto.getNewSensorName(),updateSensorDto.getDeviceName(), user);

        if(sensorUpdated) return ResponseEntity.status(200).body("Sensor updated");
        else return ResponseEntity.status(500).body("Failed to update sensor");
    }
}
