package com.manikanta.sensor.service;

import com.manikanta.common.dto.SensorEvent;
import com.manikanta.common.model.AlertLevel;
import com.manikanta.sensor.model.SensorReading;
import com.manikanta.sensor.repository.SensorReadingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SensorEventConsumerTest {

    @Mock
    private SensorReadingRepository readingRepository;

    @InjectMocks
    private SensorEventConsumer sensorEventConsumer;

    @Test
    void testConsumeReading() {
        SensorEvent event = SensorEvent.builder()
                .deviceCode("D1")
                .temperature(25.0)
                .alertLevel(AlertLevel.NORMAL)
                .timestamp(LocalDateTime.now())
                .build();

        sensorEventConsumer.consumeReading(event);

        verify(readingRepository).save(any(SensorReading.class));
    }
}
