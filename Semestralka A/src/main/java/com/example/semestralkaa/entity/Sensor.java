package com.example.semestralkaa.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sensorId;

    @Column
    private Double measuredValue;

    @ManyToOne
    private MeasuringDevice measuringDevice;
}
