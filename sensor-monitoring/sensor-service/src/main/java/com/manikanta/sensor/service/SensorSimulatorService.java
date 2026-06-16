package com.manikanta.sensor.service;

import com.manikanta.common.model.AlertLevel;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SensorSimulatorService {

    private final Random random = new Random();

    public double generateTemperature() {
        return 15.0 + (random.nextDouble() * 25.0);
    }

    public AlertLevel evaluateAlertLevel(double temperature) {
        if (temperature > 35.0) {
            return AlertLevel.CRITICAL;
        } else if (temperature > 30.0) {
            return AlertLevel.WARNING;
        }
        return AlertLevel.NORMAL;
    }
}