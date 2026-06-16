package com.manikanta.sensormonitoring.service;

import com.manikanta.sensormonitoring.dto.DeviceRequest;
import com.manikanta.sensormonitoring.dto.DeviceResponse;
import com.manikanta.sensormonitoring.exception.DuplicateResourceException;
import com.manikanta.sensormonitoring.exception.ResourceNotFoundException;
import com.manikanta.sensormonitoring.model.Device;
import com.manikanta.sensormonitoring.model.DeviceStatus;
import com.manikanta.sensormonitoring.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Transactional
    public DeviceResponse createDevice(DeviceRequest request) {
        if (deviceRepository.existsByDeviceCode(request.getDeviceCode())) {
            throw new DuplicateResourceException(
                    "Device with code '" + request.getDeviceCode() + "' already exists");
        }

        Device device = Device.builder()
                .deviceCode(request.getDeviceCode())
                .name(request.getName())
                .location(request.getLocation())
                .status(request.getStatus() != null ? request.getStatus() : DeviceStatus.ACTIVE)
                .build();

        return toResponse(deviceRepository.save(device));
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getAllDevices() {
        return deviceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeviceResponse getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
        return toResponse(device);
    }

    @Transactional(readOnly = true)
    public DeviceResponse getDeviceByCode(String deviceCode) {
        Device device = deviceRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with code: " + deviceCode));
        return toResponse(device);
    }

    @Transactional
    public DeviceResponse updateDevice(Long id, DeviceRequest request) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        // If deviceCode is being changed, make sure new code isn't already taken
        if (!device.getDeviceCode().equals(request.getDeviceCode())
                && deviceRepository.existsByDeviceCode(request.getDeviceCode())) {
            throw new DuplicateResourceException(
                    "Device with code '" + request.getDeviceCode() + "' already exists");
        }

        device.setDeviceCode(request.getDeviceCode());
        device.setName(request.getName());
        device.setLocation(request.getLocation());
        if (request.getStatus() != null) {
            device.setStatus(request.getStatus());
        }

        return toResponse(deviceRepository.save(device));
    }

    @Transactional
    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Device not found with id: " + id);
        }
        deviceRepository.deleteById(id);
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
