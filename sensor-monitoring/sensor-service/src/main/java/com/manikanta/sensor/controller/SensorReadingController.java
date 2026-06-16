package com.manikanta.sensor.controller;

import com.manikanta.sensor.dto.DeviceHealthResponse;
import com.manikanta.sensor.dto.DeviceStatusRequest;
import com.manikanta.sensor.dto.DeviceStatusResponse;
import com.manikanta.sensor.dto.SensorReadingResponse;
import com.manikanta.sensor.service.SensorReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
@Tag(name = "Sensors", description = "Endpoints for sensor readings and alerts")
@SecurityRequirement(name = "bearerAuth")
public class SensorReadingController {

    private final SensorReadingService readingService;

    @GetMapping("/readings")
    @Operation(summary = "Get all sensor readings (paginated)")
    public ResponseEntity<Page<SensorReadingResponse>> getAllReadings(Pageable pageable) {
        return ResponseEntity.ok(readingService.getAllReadings(pageable));
    }

    @GetMapping("/readings/{deviceCode}")
    @Operation(summary = "Get readings for a specific device")
    public ResponseEntity<Page<SensorReadingResponse>> getReadingsByDevice(
            @PathVariable String deviceCode, Pageable pageable) {
        return ResponseEntity.ok(readingService.getReadingsByDevice(deviceCode, pageable));
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get all critical alerts")
    public ResponseEntity<Page<SensorReadingResponse>> getAlerts(Pageable pageable) {
        return ResponseEntity.ok(readingService.getAlerts(pageable));
    }

    @PostMapping("/simulate/{deviceCode}")
    @Operation(summary = "Manually trigger a simulated reading for a device")
    public ResponseEntity<SensorReadingResponse> simulateReading(@PathVariable String deviceCode) {
        return ResponseEntity.ok(readingService.triggerManualReading(deviceCode));
    }

    @PostMapping("/getDeviceStatus")
    @Operation(summary = "Get health status for specific devices")
    public ResponseEntity<List<DeviceHealthResponse>> getDeviceStatus(@RequestBody DeviceStatusRequest request) {
        return ResponseEntity.ok(readingService.getDeviceStatus(request));
    }

    @PostMapping("/getStatus")
    @Operation(summary = "Get status of devices with paging")
    public ResponseEntity<Page<DeviceStatusResponse>> getStatus(@RequestBody DeviceStatusRequest request, Pageable pageable) {
        return ResponseEntity.ok(readingService.getStatus(request, pageable));
    }
}