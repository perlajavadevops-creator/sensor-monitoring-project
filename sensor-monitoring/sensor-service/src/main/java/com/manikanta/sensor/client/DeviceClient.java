package com.manikanta.sensor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "device-service", url = "${app.device-service.url}")
public interface DeviceClient {

    @GetMapping("/api/v1/devices/code/{deviceCode}")
    Object getDeviceByCode(@PathVariable("deviceCode") String deviceCode);

    @GetMapping("/api/v1/devices")
    List<Object> getAllDevices();
}