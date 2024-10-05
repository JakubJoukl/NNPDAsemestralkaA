package com.example.semestralkaa.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UpdateDeviceDto {
    @Getter
    @Setter
    String deviceName;

    @Getter
    @Setter
    private List<AddMeasuringDeviceSensorDto> sensors;
}
