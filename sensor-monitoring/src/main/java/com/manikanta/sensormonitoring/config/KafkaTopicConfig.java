package com.manikanta.sensormonitoring.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String SENSOR_READINGS_TOPIC = "sensor-readings";
    public static final String SENSOR_ALERTS_TOPIC = "sensor-alerts";

    @Bean
    public NewTopic sensorReadingsTopic() {
        return TopicBuilder.name(SENSOR_READINGS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sensorAlertsTopic() {
        return TopicBuilder.name(SENSOR_ALERTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
