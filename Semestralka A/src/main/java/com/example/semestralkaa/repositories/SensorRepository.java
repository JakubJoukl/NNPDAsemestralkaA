package com.example.semestralkaa.repositories;

import com.example.semestralkaa.entity.MeasuringDevice;
import com.example.semestralkaa.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Optional<Sensor> getSensorBySensorNameAndMeasuringDevice(String sensorName, MeasuringDevice measuringDevice);
}
