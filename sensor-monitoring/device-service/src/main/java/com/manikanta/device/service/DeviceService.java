package com.manikanta.device.service;

import com.manikanta.device.dto.DeviceRequest;
import com.manikanta.device.dto.DeviceResponse;
import com.manikanta.common.exception.DuplicateResourceException;
import com.manikanta.common.exception.ResourceNotFoundException;
import com.manikanta.device.model.Device;
import com.manikanta.device.model.DeviceStatus;
import com.manikanta.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Transactional
    public DeviceResponse createDevice(DeviceRequest request) {
        log.info("Creating new device with code: {}", request.getDeviceCode());
        if (deviceRepository.existsByDeviceCode(request.getDeviceCode())) {
            log.error("Duplicate device code: {}", request.getDeviceCode());
            throw new DuplicateResourceException(
                    "Device with code '" + request.getDeviceCode() + "' already exists");
        }

        Device device = Device.builder()
                .deviceCode(request.getDeviceCode())
                .name(request.getName())
                .location(request.getLocation())
                .status(request.getStatus() != null ? request.getStatus() : DeviceStatus.ACTIVE)
                .build();

        Device savedDevice = deviceRepository.save(device);
        log.info("Device created successfully with id: {}", savedDevice.getId());
        return toResponse(savedDevice);
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getAllDevices() {
        log.info("Fetching all devices");
        return deviceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeviceResponse getDeviceById(Long id) {
        log.info("Fetching device with id: {}", id);
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device not found with id: {}", id);
                    return new ResourceNotFoundException("Device not found with id: " + id);
                });
        return toResponse(device);
    }

    @Transactional(readOnly = true)
    public DeviceResponse getDeviceByCode(String deviceCode) {
        log.info("Fetching device with code: {}", deviceCode);
        Device device = deviceRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> {
                    log.error("Device not found with code: {}", deviceCode);
                    return new ResourceNotFoundException("Device not found with code: " + deviceCode);
                });
        return toResponse(device);
    }

    @Transactional
    public DeviceResponse updateDevice(Long id, DeviceRequest request) {
        log.info("Updating device with id: {}", id);
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device not found with id: {}", id);
                    return new ResourceNotFoundException("Device not found with id: " + id);
                });

        if (!device.getDeviceCode().equals(request.getDeviceCode())
                && deviceRepository.existsByDeviceCode(request.getDeviceCode())) {
            log.error("Duplicate device code: {} for update", request.getDeviceCode());
            throw new DuplicateResourceException(
                    "Device with code '" + request.getDeviceCode() + "' already exists");
        }

        device.setDeviceCode(request.getDeviceCode());
        device.setName(request.getName());
        device.setLocation(request.getLocation());
        if (request.getStatus() != null) {
            device.setStatus(request.getStatus());
        }

        Device updatedDevice = deviceRepository.save(device);
        log.info("Device updated successfully with id: {}", updatedDevice.getId());
        return toResponse(updatedDevice);
    }

    @Transactional
    public void deleteDevice(Long id) {
        log.info("Deleting device with id: {}", id);
        if (!deviceRepository.existsById(id)) {
            log.error("Device not found with id: {} for deletion", id);
            throw new ResourceNotFoundException("Device not found with id: " + id);
        }
        deviceRepository.deleteById(id);
        log.info("Device deleted successfully with id: {}", id);
    }

    private DeviceResponse toResponse(Device device) {
        return DeviceResponse.builder()
                .id(device.getId())
                .deviceCode(device.getDeviceCode())
                .name(device.getName())
                .location(device.getLocation())
                .status(device.getStatus())
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .build();
    }
}