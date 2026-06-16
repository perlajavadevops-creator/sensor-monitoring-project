package com.manikanta.sensormonitoring.service;

import com.manikanta.sensormonitoring.dto.SensorEvent;
import com.manikanta.sensormonitoring.model.SensorReading;
import com.manikanta.sensormonitoring.repository.SensorReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.manikanta.sensormonitoring.config.KafkaTopicConfig.SENSOR_ALERTS_TOPIC;
import static com.manikanta.sensormonitoring.config.KafkaTopicConfig.SENSOR_READINGS_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorEventConsumer {

    private final SensorReadingRepository sensorReadingRepository;

    @KafkaListener(topics = SENSOR_READINGS_TOPIC, groupId = "sensor-monitoring-group")
    public void consumeReading(SensorEvent event) {
        SensorReading reading = SensorReading.builder()
                .deviceCode(event.getDeviceCode())
                .temperature(event.getTemperature())
                .alertLevel(event.getAlertLevel())
                .build();

        sensorReadingRepository.save(reading);
        log.info("Persisted reading for device {}: {} C [{}]",
                event.getDeviceCode(), event.getTemperature(), event.getAlertLevel());
    }

    @KafkaListener(topics = SENSOR_ALERTS_TOPIC, groupId = "sensor-monitoring-alert-group")
    public void consumeAlert(SensorEvent event) {
        // In a real system this could trigger notifications (email/SMS/Slack etc.)
        log.warn(">>> ALERT! Device {} is at {} C - status: {}",
                event.getDeviceCode(), event.getTemperature(), event.getAlertLevel());
    }
}
