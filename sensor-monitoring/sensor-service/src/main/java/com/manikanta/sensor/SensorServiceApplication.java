package com.manikanta.sensor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@ComponentScan(basePackages = {"com.manikanta.sensor", "com.manikanta.common"})
public class SensorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SensorServiceApplication.class, args);
    }
}