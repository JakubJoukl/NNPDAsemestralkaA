package com.example.semestralkaa.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sensorId;

    @Column
    private String sensorName;

    @ManyToOne
    private MeasuringDevice measuringDevice;

    private transient Double measuredValue;

    public Sensor(String sensorName){
        this.sensorName = sensorName;
    }
}
