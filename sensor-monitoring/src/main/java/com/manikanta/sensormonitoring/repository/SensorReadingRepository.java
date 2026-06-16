package com.manikanta.sensormonitoring.repository;

import com.manikanta.sensormonitoring.model.AlertLevel;
import com.manikanta.sensormonitoring.model.SensorReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    Page<SensorReading> findByDeviceCode(String deviceCode, Pageable pageable);

    Page<SensorReading> findByAlertLevel(AlertLevel alertLevel, Pageable pageable);

    Page<SensorReading> findByDeviceCodeAndAlertLevel(String deviceCode, AlertLevel alertLevel, Pageable pageable);
}
