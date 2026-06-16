package com.manikanta.sensor.repository;

import com.manikanta.common.model.AlertLevel;
import com.manikanta.sensor.model.SensorReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    Page<SensorReading> findByDeviceCode(String deviceCode, Pageable pageable);
    Page<SensorReading> findByAlertLevel(AlertLevel alertLevel, Pageable pageable);

    @Query("SELECT r FROM SensorReading r WHERE r.deviceCode = :deviceCode ORDER BY r.recordedAt DESC LIMIT 1")
    Optional<SensorReading> findLatestByDeviceCode(@Param("deviceCode") String deviceCode);

    @Query("SELECT COUNT(r) FROM SensorReading r WHERE r.deviceCode = :deviceCode AND r.recordedAt >= :since")
    long countPacketsByDeviceCodeSince(@Param("deviceCode") String deviceCode, @Param("since") LocalDateTime since);

    @Query("SELECT DISTINCT r.deviceCode FROM SensorReading r")
    List<String> findAllDeviceCodes();
}