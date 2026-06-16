package com.manikanta.sensor.service;

import com.manikanta.common.dto.SensorEvent;
import com.manikanta.common.model.AlertLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SensorEventProducerTest {

    @Mock
    private KafkaTemplate<String, SensorEvent> kafkaTemplate;

    @InjectMocks
    private SensorEventProducer sensorEventProducer;

    @Test
    void testPublishReading() {
        SensorEvent event = SensorEvent.builder()
                .deviceCode("D1")
                .temperature(25.0)
                .alertLevel(AlertLevel.NORMAL)
                .timestamp(LocalDateTime.now())
                .build();

        sensorEventProducer.publishReading(event);

        verify(kafkaTemplate).send(eq("sensor-readings"), eq("D1"), eq(event));
    }

    @Test
    void testPublishAlert() {
        SensorEvent event = SensorEvent.builder()
                .deviceCode("D1")
                .temperature(40.0)
                .alertLevel(AlertLevel.CRITICAL)
                .timestamp(LocalDateTime.now())
                .build();

        sensorEventProducer.publishAlert(event);

        verify(kafkaTemplate).send(eq("sensor-alerts"), eq("D1"), eq(event));
    }
}
