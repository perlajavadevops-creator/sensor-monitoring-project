package com.manikanta.sensormonitoring.service;

import com.manikanta.sensormonitoring.model.AlertLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates a temperature sensor / router.
 *
 * Behaviour (as discussed in POC meeting):
 *  - Normal range: 30.0 C to 70.0 C  -> 90% probability
 *  - High range:   70.1 C to 200.0 C -> 10% probability
 *  - If temperature > 70.0 C => ALERT (WARNING / CRITICAL)
 */
@Service
@Slf4j
public class SensorSimulatorService {

    private static final double NORMAL_MIN = 30.0;
    private static final double NORMAL_MAX = 70.0;
    private static final double HIGH_MIN = 70.1;
    private static final double HIGH_MAX = 200.0;

    private static final double NORMAL_PROBABILITY = 0.90; // 90%
    private static final double CRITICAL_THRESHOLD = 90.0;  // >90 -> CRITICAL, else WARNING

    /**
     * Generates a simulated temperature reading.
     */
    public double generateTemperature() {
        double roll = ThreadLocalRandom.current().nextDouble(); // 0.0 - 1.0

        double temperature;
        if (roll < NORMAL_PROBABILITY) {
            // 90% chance -> 30.0 to 70.0
            temperature = randomInRange(NORMAL_MIN, NORMAL_MAX);
        } else {
            // 10% chance -> 70.1 to 200.0
            temperature = randomInRange(HIGH_MIN, HIGH_MAX);
        }

        return round(temperature);
    }

    /**
     * Determines alert level based on temperature.
     *  <= 70.0   => NORMAL
     *  70.1-90.0 => WARNING (overheating)
     *  > 90.0    => CRITICAL
     */
    public AlertLevel evaluateAlertLevel(double temperature) {
        if (temperature > CRITICAL_THRESHOLD) {
            return AlertLevel.CRITICAL;
        } else if (temperature > NORMAL_MAX) {
            return AlertLevel.WARNING;
        }
        return AlertLevel.NORMAL;
    }

    private double randomInRange(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
