package com.example.semestralkaa.dto;

import lombok.Getter;
import lombok.Setter;

public class SensorDto {
    @Getter
    @Setter
    private MeasuringDeviceDto measuringDeviceDto;

    @Getter
    @Setter
    private String sensorName;
}
