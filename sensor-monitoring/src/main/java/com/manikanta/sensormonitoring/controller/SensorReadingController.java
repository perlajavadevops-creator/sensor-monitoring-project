package com.manikanta.sensormonitoring.controller;

import com.manikanta.sensormonitoring.dto.SensorReadingResponse;
import com.manikanta.sensormonitoring.service.SensorReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/readings")
@RequiredArgsConstructor
@Tag(name = "Sensor Readings", description = "View simulated sensor data and alerts")
public class SensorReadingController {

    private final SensorReadingService sensorReadingService;

    @Operation(summary = "Get all sensor readings (paginated)")
    @GetMapping
    public ResponseEntity<Page<SensorReadingResponse>> getAllReadings(
            @PageableDefault(size = 20, sort = "recordedAt") Pageable pageable) {
        return ResponseEntity.ok(sensorReadingService.getAllReadings(pageable));
    }

    @Operation(summary = "Get readings for a specific device")
    @GetMapping("/device/{deviceCode}")
    public ResponseEntity<Page<SensorReadingResponse>> getReadingsByDevice(
            @PathVariable String deviceCode,
            @PageableDefault(size = 20, sort = "recordedAt") Pageable pageable) {
        return ResponseEntity.ok(sensorReadingService.getReadingsByDevice(deviceCode, pageable));
    }

    @Operation(summary = "Get all CRITICAL (overheating) alerts")
    @GetMapping("/alerts")
    public ResponseEntity<Page<SensorReadingResponse>> getAlerts(
            @PageableDefault(size = 20, sort = "recordedAt") Pageable pageable) {
        return ResponseEntity.ok(sensorReadingService.getAlerts(pageable));
    }

    @Operation(summary = "Trigger a manual simulated reading for a device (for testing)")
    @PostMapping("/simulate/{deviceCode}")
    public ResponseEntity<SensorReadingResponse> triggerManualReading(@PathVariable String deviceCode) {
        return ResponseEntity.ok(sensorReadingService.triggerManualReading(deviceCode));
    }
}
