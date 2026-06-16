package com.manikanta.sensormonitoring.dto;

import com.manikanta.sensormonitoring.model.DeviceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
