package com.manikanta.sensormonitoring.dto;

import com.manikanta.sensormonitoring.model.AlertLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorEvent implements Serializable {
    private String deviceCode;
    private Double temperature;
    private AlertLevel alertLevel;
    private LocalDateTime timestamp;
}
