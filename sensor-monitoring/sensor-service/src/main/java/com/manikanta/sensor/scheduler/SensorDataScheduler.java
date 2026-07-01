package com.manikanta.sensor.scheduler;

import com.manikanta.sensor.client.DeviceClient;
import com.manikanta.sensor.service.SensorReadingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SensorDataScheduler {

    private final SensorReadingService readingService;
    private final DeviceClient deviceClient;

    @Scheduled(fixedRateString = "${app.sensor.interval-ms:30000}")
    @CircuitBreaker(name = "deviceServiceCircuitBreaker", fallbackMethod = "deviceFallback")
    public void generateSensorData() {
        log.info("Starting scheduled sensor data generation...");
        try {
            // Synchronous call to device-service via Feign
            List<Object> devices = deviceClient.getAllDevices();

            if (devices.isEmpty()) {
                log.warn("No devices found in device-service. Skipping data generation.");
                return;
            }

            for (Object deviceObj : devices) {
                // Assuming device response has a deviceCode field
                if (deviceObj instanceof Map) {
                    String deviceCode = (String) ((Map<?, ?>) deviceObj).get("deviceCode");
                    if (deviceCode != null) {
                        readingService.triggerManualReading(deviceCode);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error during scheduled sensor data generation: {}", e.getMessage());
        }
    }

    // Fallback method for CircuitBreaker - must have same signature plus Throwable
    public void deviceFallback(Throwable t) {
        log.error("Device service is unavailable - circuit breaker fallback triggered: {}", t == null ? "unknown" : t.getMessage());
        // Optionally, we could emit synthetic sensor events or skip the cycle.
    }
}