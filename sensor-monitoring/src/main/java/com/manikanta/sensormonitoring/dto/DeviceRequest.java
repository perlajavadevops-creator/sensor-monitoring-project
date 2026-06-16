package com.manikanta.sensormonitoring.dto;

import com.manikanta.sensormonitoring.model.DeviceStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRequest {

    @NotBlank(message = "deviceCode is required")
    private String deviceCode;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "location is required")
    private String location;

    private DeviceStatus status;
}
