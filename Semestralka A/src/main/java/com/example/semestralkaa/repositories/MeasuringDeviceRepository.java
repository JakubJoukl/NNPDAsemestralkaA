package com.example.semestralkaa.repositories;

import com.example.semestralkaa.entity.MeasuringDevice;
import com.example.semestralkaa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeasuringDeviceRepository extends JpaRepository<MeasuringDevice, Integer> {
    public Optional<MeasuringDevice> getMeasuringDeviceByDeviceNameAndUser(String measuringDeviceName, User username);
}
