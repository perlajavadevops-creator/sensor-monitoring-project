package com.manikanta.device.controller;

import com.manikanta.device.dto.DeviceRequest;
import com.manikanta.device.dto.DeviceResponse;
import com.manikanta.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Devices", description = "Endpoints for managing sensor devices")
@SecurityRequirement(name = "bearerAuth")
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    @Operation(summary = "Register a new device")
    public ResponseEntity<DeviceResponse> createDevice(@Valid @RequestBody DeviceRequest request) {
        log.info("REST request to create Device : {}", request);
        return new ResponseEntity<>(deviceService.createDevice(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all registered devices")
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {
        log.info("REST request to get all Devices");
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get device by ID")
    public ResponseEntity<DeviceResponse> getDeviceById(@PathVariable Long id) {
        log.info("REST request to get Device : {}", id);
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @GetMapping("/code/{deviceCode}")
    @Operation(summary = "Get device by its unique code")
    public ResponseEntity<DeviceResponse> getDeviceByCode(@PathVariable String deviceCode) {
        log.info("REST request to get Device by code : {}", deviceCode);
        return ResponseEntity.ok(deviceService.getDeviceByCode(deviceCode));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing device")
    public ResponseEntity<DeviceResponse> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceRequest request) {
        log.info("REST request to update Device : {}, {}", id, request);
        return ResponseEntity.ok(deviceService.updateDevice(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a device")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDevice(@PathVariable Long id) {
        log.info("REST request to delete Device : {}", id);
        deviceService.deleteDevice(id);
    }
}