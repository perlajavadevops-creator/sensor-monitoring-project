package com.manikanta.sensormonitoring.controller;

import com.manikanta.sensormonitoring.dto.DeviceRequest;
import com.manikanta.sensormonitoring.dto.DeviceResponse;
import com.manikanta.sensormonitoring.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Tag(name = "Device", description = "CRUD operations for sensor devices")
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "Create a new device")
    @PostMapping
    public ResponseEntity<DeviceResponse> createDevice(@Valid @RequestBody DeviceRequest request) {
        return new ResponseEntity<>(deviceService.createDevice(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all devices")
    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @Operation(summary = "Get a device by id")
    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @Operation(summary = "Get a device by device code")
    @GetMapping("/code/{deviceCode}")
    public ResponseEntity<DeviceResponse> getDeviceByCode(@PathVariable String deviceCode) {
        return ResponseEntity.ok(deviceService.getDeviceByCode(deviceCode));
    }

    @Operation(summary = "Update a device")
    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> updateDevice(@PathVariable Long id,
                                                         @Valid @RequestBody DeviceRequest request) {
        return ResponseEntity.ok(deviceService.updateDevice(id, request));
    }

    @Operation(summary = "Delete a device")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}
