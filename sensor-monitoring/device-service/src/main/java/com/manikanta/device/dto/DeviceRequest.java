package com.manikanta.device.dto;

import com.manikanta.device.model.DeviceStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceRequest {
    @NotBlank
    private String deviceCode;
    @NotBlank
    private String name;
    private String location;
    private DeviceStatus status;
}