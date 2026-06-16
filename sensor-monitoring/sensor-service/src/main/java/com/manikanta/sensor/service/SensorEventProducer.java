package com.manikanta.sensor.service;

import com.manikanta.common.dto.SensorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEventProducer {

    private final KafkaTemplate<String, SensorEvent> kafkaTemplate;

    private static final String READINGS_TOPIC = "sensor-readings";
    private static final String ALERTS_TOPIC = "sensor-alerts";

    public void publishReading(SensorEvent event) {
        log.info("Publishing reading: {}", event);
        kafkaTemplate.send(READINGS_TOPIC, event.getDeviceCode(), event);
    }

    public void publishAlert(SensorEvent event) {
        log.warn("Publishing ALERT: {}", event);
        kafkaTemplate.send(ALERTS_TOPIC, event.getDeviceCode(), event);
    }
}