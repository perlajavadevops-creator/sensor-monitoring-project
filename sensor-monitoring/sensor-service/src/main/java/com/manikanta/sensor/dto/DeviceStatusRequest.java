package com.manikanta.sensor.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeviceStatusRequest {
    private List<String> deviceIds;
}