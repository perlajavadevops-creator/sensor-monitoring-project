package com.manikanta.sensor.service;

import com.manikanta.common.model.AlertLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SensorSimulatorServiceTest {

    private SensorSimulatorService simulatorService;

    @BeforeEach
    void setUp() {
        simulatorService = new SensorSimulatorService();
    }

    @Test
    void testGenerateTemperature() {
        double temp = simulatorService.generateTemperature();
        assertTrue(temp >= 15.0 && temp <= 40.0, "Temperature should be between 15 and 40");
    }

    @Test
    void testEvaluateAlertLevelNormal() {
        assertEquals(AlertLevel.NORMAL, simulatorService.evaluateAlertLevel(25.0));
        assertEquals(AlertLevel.NORMAL, simulatorService.evaluateAlertLevel(30.0));
    }

    @Test
    void testEvaluateAlertLevelWarning() {
        assertEquals(AlertLevel.WARNING, simulatorService.evaluateAlertLevel(31.0));
        assertEquals(AlertLevel.WARNING, simulatorService.evaluateAlertLevel(35.0));
    }

    @Test
    void testEvaluateAlertLevelCritical() {
        assertEquals(AlertLevel.CRITICAL, simulatorService.evaluateAlertLevel(35.1));
        assertEquals(AlertLevel.CRITICAL, simulatorService.evaluateAlertLevel(40.0));
    }
}
