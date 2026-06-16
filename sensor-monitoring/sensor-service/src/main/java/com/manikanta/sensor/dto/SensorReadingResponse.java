package com.manikanta.sensor.dto;

import com.manikanta.common.model.AlertLevel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SensorReadingResponse {
    private Long id;
    private String deviceCode;
    private Double temperature;
    private AlertLevel alertLevel;
    private LocalDateTime recordedAt;
}