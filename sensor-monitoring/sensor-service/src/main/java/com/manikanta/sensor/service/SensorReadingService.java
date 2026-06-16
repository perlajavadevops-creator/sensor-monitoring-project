package com.manikanta.sensor.service;

import com.manikanta.common.dto.SensorEvent;
import com.manikanta.sensor.dto.DeviceHealthResponse;
import com.manikanta.sensor.dto.DeviceStatusRequest;
import com.manikanta.sensor.dto.DeviceStatusResponse;
import com.manikanta.sensor.dto.SensorReadingResponse;
import com.manikanta.common.model.AlertLevel;
import com.manikanta.sensor.model.SensorReading;
import com.manikanta.sensor.repository.SensorReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorReadingService {

    private final SensorReadingRepository sensorReadingRepository;
    private final SensorSimulatorService simulatorService;
    private final SensorEventProducer eventProducer;

    @Transactional(readOnly = true)
    public Page<SensorReadingResponse> getAllReadings(Pageable pageable) {
        return sensorReadingRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<SensorReadingResponse> getReadingsByDevice(String deviceCode, Pageable pageable) {
        return sensorReadingRepository.findByDeviceCode(deviceCode, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<SensorReadingResponse> getAlerts(Pageable pageable) {
        return sensorReadingRepository.findByAlertLevel(AlertLevel.CRITICAL, pageable)
                .map(this::toResponse);
    }

    public SensorReadingResponse triggerManualReading(String deviceCode) {
        double temperature = simulatorService.generateTemperature();
        AlertLevel alertLevel = simulatorService.evaluateAlertLevel(temperature);

        SensorEvent event = SensorEvent.builder()
                .deviceCode(deviceCode)
                .temperature(temperature)
                .alertLevel(alertLevel)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        eventProducer.publishReading(event);
        if (alertLevel != AlertLevel.NORMAL) {
            eventProducer.publishAlert(event);
        }

        return SensorReadingResponse.builder()
                .deviceCode(deviceCode)
                .temperature(temperature)
                .alertLevel(alertLevel)
                .recordedAt(event.getTimestamp())
                .build();
    }

    @Transactional(readOnly = true)
    public List<DeviceHealthResponse> getDeviceStatus(DeviceStatusRequest request) {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        return request.getDeviceIds().stream()
                .map(deviceId -> {
                    SensorReading latest = sensorReadingRepository.findLatestByDeviceCode(deviceId).orElse(null);
                    long packets = sensorReadingRepository.countPacketsByDeviceCodeSince(deviceId, tenMinutesAgo);
                    boolean isAlive = latest != null && latest.getRecordedAt().isAfter(tenMinutesAgo);
                    
                    return DeviceHealthResponse.builder()
                            .deviceId(deviceId)
                            .temp(latest != null ? latest.getTemperature() : null)
                            .num_packets(packets)
                            .health(isAlive ? "ALIVE" : "DEAD")
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<DeviceStatusResponse> getStatus(DeviceStatusRequest request, Pageable pageable) {
        List<String> deviceIds = request.getDeviceIds();
        if (deviceIds == null || deviceIds.isEmpty()) {
            deviceIds = sensorReadingRepository.findAllDeviceCodes();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), deviceIds.size());
        
        if (start > deviceIds.size()) {
            return new PageImpl<>(List.of(), pageable, deviceIds.size());
        }

        List<String> pagedIds = deviceIds.subList(start, end);
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        List<DeviceStatusResponse> content = pagedIds.stream()
                .map(deviceId -> {
                    SensorReading latest = sensorReadingRepository.findLatestByDeviceCode(deviceId).orElse(null);
                    long packets = sensorReadingRepository.countPacketsByDeviceCodeSince(deviceId, tenMinutesAgo);
                    boolean isAlive = latest != null && latest.getRecordedAt().isAfter(tenMinutesAgo);

                    return DeviceStatusResponse.builder()
                            .deviceId(deviceId)
                            .temp(latest != null ? latest.getTemperature() : null)
                            .num_packets(packets)
                            .alive(isAlive)
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, deviceIds.size());
    }

    private SensorReadingResponse toResponse(SensorReading reading) {
        return SensorReadingResponse.builder()
                .id(reading.getId())
                .deviceCode(reading.getDeviceCode())
                .temperature(reading.getTemperature())
                .alertLevel(reading.getAlertLevel())
                .recordedAt(reading.getRecordedAt())
                .build();
    }
}