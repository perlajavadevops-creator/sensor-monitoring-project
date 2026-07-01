package com.manikanta.sensor.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String READINGS_TOPIC = "sensor-readings";
    public static final String ALERTS_TOPIC = "sensor-alerts";

    @Bean
    public NewTopic readingsTopic() {
        return TopicBuilder.name(READINGS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic alertsTopic() {
        return TopicBuilder.name(ALERTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}