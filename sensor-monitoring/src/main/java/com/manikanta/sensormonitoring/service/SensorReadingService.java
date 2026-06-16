package com.manikanta.sensormonitoring.service;

import com.manikanta.sensormonitoring.dto.SensorEvent;
import com.manikanta.sensormonitoring.dto.SensorReadingResponse;
import com.manikanta.sensormonitoring.model.AlertLevel;
import com.manikanta.sensormonitoring.model.SensorReading;
import com.manikanta.sensormonitoring.repository.SensorReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Triggers an on-demand simulated reading for a given device code
     * (useful for testing via Postman / Echo API without waiting for the scheduler).
     */
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
