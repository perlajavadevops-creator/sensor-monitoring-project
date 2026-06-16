package com.manikanta.sensormonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SensorMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SensorMonitoringApplication.class, args);
    }

}
