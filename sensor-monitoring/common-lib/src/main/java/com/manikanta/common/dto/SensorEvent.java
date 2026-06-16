package com.manikanta.common.dto;

import com.manikanta.common.model.AlertLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorEvent {
    private String deviceCode;
    private Double temperature;
    private AlertLevel alertLevel;
    private LocalDateTime timestamp;
}