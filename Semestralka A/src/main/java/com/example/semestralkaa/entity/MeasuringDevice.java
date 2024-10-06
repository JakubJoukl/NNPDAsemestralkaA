package com.example.semestralkaa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(
        name="MEASURING_DEVICE",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"device_name", "user_user_id"})
)
public class MeasuringDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer measuringDeviceId;

    @Column(length = 255)
    @NotBlank
    private String deviceName;

    @OneToMany(mappedBy = "measuringDevice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sensor> sensors = new ArrayList<>();

    @ManyToOne
    @NotNull
    private User user;

    public MeasuringDevice(String deviceName, List<Sensor> sensors, User user) {
        this.deviceName = deviceName;
        this.user = user;
        this.sensors = sensors;
    }
}
