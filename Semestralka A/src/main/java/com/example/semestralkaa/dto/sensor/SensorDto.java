package com.example.semestralkaa.dto.sensor;

import com.example.semestralkaa.dto.measuringDevice.MeasuringDeviceDto;
import lombok.Getter;
import lombok.Setter;

public class SensorDto {
    @Getter
    @Setter
    private MeasuringDeviceDto measuringDeviceDto;

    @Getter
    @Setter
    private String sensorName;

    @Getter
    @Setter
    private Integer measuredValue;
}
