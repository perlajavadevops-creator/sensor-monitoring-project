package com.manikanta.sensor.service;

import com.manikanta.common.dto.SensorEvent;
import com.manikanta.sensor.model.SensorReading;
import com.manikanta.sensor.repository.SensorReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEventConsumer {

    private final SensorReadingRepository readingRepository;

    @KafkaListener(topics = "sensor-readings", groupId = "sensor-monitoring-group")
    public void consumeReading(SensorEvent event) {
        log.info("Consumed reading for device {}: {}°C", event.getDeviceCode(), event.getTemperature());

        SensorReading reading = SensorReading.builder()
                .deviceCode(event.getDeviceCode())
                .temperature(event.getTemperature())
                .alertLevel(event.getAlertLevel())
                .recordedAt(event.getTimestamp())
                .build();

        readingRepository.save(reading);
    }

    @KafkaListener(topics = "sensor-alerts", groupId = "sensor-alerts-group")
    public void consumeAlert(SensorEvent event) {
        log.error("ALERT RECEIVED! Device: {}, Temp: {}°C, Level: {}",
                event.getDeviceCode(), event.getTemperature(), event.getAlertLevel());
    }
}