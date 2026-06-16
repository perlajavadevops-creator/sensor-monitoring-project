package com.manikanta.sensor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceStatusResponse {
    private String deviceId;
    private Double temp;
    private Long num_packets;
    private Boolean alive;
}