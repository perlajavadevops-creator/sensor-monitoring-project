package com.manikanta.sensormonitoring.scheduler;

import com.manikanta.sensormonitoring.dto.SensorEvent;
import com.manikanta.sensormonitoring.model.AlertLevel;
import com.manikanta.sensormonitoring.model.Device;
import com.manikanta.sensormonitoring.model.DeviceStatus;
import com.manikanta.sensormonitoring.repository.DeviceRepository;
import com.manikanta.sensormonitoring.service.SensorEventProducer;
import com.manikanta.sensormonitoring.service.SensorSimulatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Periodically generates simulated temperature readings for every ACTIVE device
 * and publishes them to Kafka. If a reading is above the normal threshold,
 * an alert event is also published.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SensorSimulationScheduler {

    private final DeviceRepository deviceRepository;
    private final SensorSimulatorService simulatorService;
    private final SensorEventProducer eventProducer;

    // Runs every 10 seconds
    @Scheduled(fixedRateString = "${sensor.simulation.interval-ms:10000}")
    public void simulateReadings() {
        List<Device> activeDevices = deviceRepository.findAll()
                .stream()
                .filter(d -> d.getStatus() == DeviceStatus.ACTIVE)
                .toList();

        if (activeDevices.isEmpty()) {
            log.debug("No active devices found, skipping simulation cycle");
            return;
        }

        for (Device device : activeDevices) {
            double temperature = simulatorService.generateTemperature();
            AlertLevel alertLevel = simulatorService.evaluateAlertLevel(temperature);

            SensorEvent event = SensorEvent.builder()
                    .deviceCode(device.getDeviceCode())
                    .temperature(temperature)
                    .alertLevel(alertLevel)
                    .timestamp(LocalDateTime.now())
                    .build();

            eventProducer.publishReading(event);

            if (alertLevel != AlertLevel.NORMAL) {
                eventProducer.publishAlert(event);
            }
        }
    }
}
