package com.manikanta.sensormonitoring.service;

import com.manikanta.sensormonitoring.config.KafkaTopicConfig;
import com.manikanta.sensormonitoring.dto.SensorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorEventProducer {

    private final KafkaTemplate<String, SensorEvent> kafkaTemplate;

    public void publishReading(SensorEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.SENSOR_READINGS_TOPIC, event.getDeviceCode(), event);
        log.debug("Published reading to '{}': {}", KafkaTopicConfig.SENSOR_READINGS_TOPIC, event);
    }

    public void publishAlert(SensorEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.SENSOR_ALERTS_TOPIC, event.getDeviceCode(), event);
        log.warn("Published ALERT to '{}': {}", KafkaTopicConfig.SENSOR_ALERTS_TOPIC, event);
    }
}
