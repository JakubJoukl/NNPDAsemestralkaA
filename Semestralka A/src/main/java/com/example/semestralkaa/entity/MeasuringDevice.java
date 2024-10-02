package com.example.semestralkaa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class MeasuringDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer measuringDeviceId;

    @Column(length = 255)
    private String name;

    @OneToMany(mappedBy = "measuringDevice")
    private List<Sensor> sensors;
}
