package com.manikanta.sensor.service;

import com.manikanta.common.model.AlertLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class SensorSimulatorService {

    private final Random random = new Random();

    public double generateTemperature() {
        double temp = 15.0 + (random.nextDouble() * 25.0);
        log.trace("Simulated temperature: {}", temp);
        return temp;
    }

    public AlertLevel evaluateAlertLevel(double temperature) {
        AlertLevel level;
        if (temperature > 35.0) {
            level = AlertLevel.CRITICAL;
        } else if (temperature > 30.0) {
            level = AlertLevel.WARNING;
        } else {
            level = AlertLevel.NORMAL;
        }
        log.trace("Evaluated alert level for {}: {}", temperature, level);
        return level;
    }
}