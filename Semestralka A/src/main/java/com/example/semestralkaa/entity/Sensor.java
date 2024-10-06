package com.example.semestralkaa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(
        name="SENSOR",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"sensor_name", "measuring_device_measuring_device_id"})
)
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sensorId;

    @Column
    @NotBlank
    private String sensorName;

    @ManyToOne
    @NotNull
    private MeasuringDevice measuringDevice;

    private transient Double measuredValue;

    public Sensor(String sensorName){
        this.sensorName = sensorName;
    }

    public Sensor(String sensorName, MeasuringDevice measuringDevice){
        this.sensorName = sensorName;
        this.measuringDevice = measuringDevice;
    }
}
