package com.manikanta.device.dto;

import com.manikanta.device.model.DeviceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeviceResponse {
    private Long id;
    private String deviceCode;
    private String name;
    private String location;
    private DeviceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}