package com.example.semestralkaa.dto.measuringDevice;

import com.example.semestralkaa.dto.measuringDevice.AddMeasuringDeviceSensorDto;
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
