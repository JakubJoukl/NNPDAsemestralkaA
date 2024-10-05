package com.example.semestralkaa.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class MeasuringDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer measuringDeviceId;

    @Column(length = 255)
    private String deviceName;

    @OneToMany(mappedBy = "measuringDevice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sensor> sensors = new ArrayList<>();

    @ManyToOne
    private User user;

    public MeasuringDevice(String deviceName, List<Sensor> sensors, User user) {
        this.deviceName = deviceName;
        this.user = user;
        this.sensors = sensors;
    }
}
