package com.manikanta.sensor.model;

import com.manikanta.common.model.AlertLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceCode;

    @Column(nullable = false)
    private Double temperature;

    @Enumerated(EnumType.STRING)
    private AlertLevel alertLevel;

    @CreationTimestamp
    private LocalDateTime recordedAt;
}