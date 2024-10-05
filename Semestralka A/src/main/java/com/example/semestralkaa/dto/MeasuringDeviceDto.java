package com.example.semestralkaa.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class MeasuringDeviceDto {

    @Getter
    @Setter
    private Integer measuringDeviceId;

    @Getter
    @Setter
    private String deviceName;

    @Getter
    @Setter
    private List<AddMeasuringDeviceSensorDto> sensors;

    @Getter
    @Setter
    private Integer userId;
}
