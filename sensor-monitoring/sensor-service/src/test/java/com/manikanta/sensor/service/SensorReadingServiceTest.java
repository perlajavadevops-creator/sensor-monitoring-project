package com.manikanta.sensor.service;

import com.manikanta.common.dto.SensorEvent;
import com.manikanta.common.model.AlertLevel;
import com.manikanta.sensor.dto.DeviceStatusRequest;
import com.manikanta.sensor.dto.DeviceStatusResponse;
import com.manikanta.sensor.dto.SensorReadingResponse;
import com.manikanta.sensor.model.SensorReading;
import com.manikanta.sensor.repository.SensorReadingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorReadingServiceTest {

    @Mock
    private SensorReadingRepository sensorReadingRepository;

    @Mock
    private SensorSimulatorService simulatorService;

    @Mock
    private SensorEventProducer eventProducer;

    @InjectMocks
    private SensorReadingService sensorReadingService;

    @Test
    void testGetAllReadings() {
        Pageable pageable = PageRequest.of(0, 10);
        SensorReading reading = SensorReading.builder().id(1L).deviceCode("D1").temperature(25.0).build();
        when(sensorReadingRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(reading)));

        Page<SensorReadingResponse> result = sensorReadingService.getAllReadings(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("D1", result.getContent().get(0).getDeviceCode());
    }

    @Test
    void testTriggerManualReading() {
        String deviceCode = "D1";
        double temp = 36.0;
        AlertLevel level = AlertLevel.CRITICAL;

        when(simulatorService.generateTemperature()).thenReturn(temp);
        when(simulatorService.evaluateAlertLevel(temp)).thenReturn(level);

        SensorReadingResponse response = sensorReadingService.triggerManualReading(deviceCode);

        assertNotNull(response);
        assertEquals(deviceCode, response.getDeviceCode());
        assertEquals(temp, response.getTemperature());
        assertEquals(level, response.getAlertLevel());

        verify(eventProducer).publishReading(any(SensorEvent.class));
        verify(eventProducer).publishAlert(any(SensorEvent.class));
    }

    @Test
    void testGetStatus() {
        DeviceStatusRequest request = new DeviceStatusRequest();
        request.setDeviceIds(List.of("D1"));
        
        Pageable pageable = PageRequest.of(0, 10);
        SensorReading latest = SensorReading.builder()
                .deviceCode("D1")
                .temperature(25.0)
                .recordedAt(LocalDateTime.now())
                .build();

        when(sensorReadingRepository.findLatestByDeviceCode("D1")).thenReturn(Optional.of(latest));
        when(sensorReadingRepository.countPacketsByDeviceCodeSince(eq("D1"), any(LocalDateTime.class))).thenReturn(5L);

        Page<DeviceStatusResponse> result = sensorReadingService.getStatus(request, pageable);

        assertEquals(1, result.getTotalElements());
        DeviceStatusResponse status = result.getContent().get(0);
        assertEquals("D1", status.getDeviceId());
        // Fix: Use getAlive() instead of isAlive() because Lombok generates getAlive() for Boolean (wrapper)
        assertTrue(status.getAlive());
        assertEquals(5L, status.getNum_packets());
    }
}
