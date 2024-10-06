package com.example.semestralkaa.dto.measuringDevice;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UpdateMeasuringDeviceDto {
    @Getter
    @Setter
    String deviceName;

    @Getter
    @Setter
    String newDeviceName;

    @Getter
    @Setter
    private List<AddMeasuringDeviceSensorDto> sensors;
}
