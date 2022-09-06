package com.example.demo.config;

import com.example.demo.config.condition.TopicCreationCondition;
import com.example.demo.config.properties.TopicProp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@Conditional(TopicCreationCondition.class)
@RequiredArgsConstructor
public class KafkaTopicBuilderConfig {
    private final TopicProp userClickEventTopicProp;
    private final TopicProp userClickEventWindowTopicProp;
    private final TopicProp clickEventWindowTopicProp;
    private final TopicProp pageClickEventWindowTopicProp;

    @Bean
    public KafkaAdmin.NewTopics newTopics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(userClickEventTopicProp.getTopic())
                        .partitions(userClickEventTopicProp.getPartition())
                        .replicas(userClickEventTopicProp.getReplicationFactor())
                        .build(),
                TopicBuilder.name(userClickEventWindowTopicProp.getTopic())
                        .partitions(userClickEventWindowTopicProp.getPartition())
                        .replicas(userClickEventWindowTopicProp.getReplicationFactor())
                        .build(),
                TopicBuilder.name(clickEventWindowTopicProp.getTopic())
                        .partitions(clickEventWindowTopicProp.getPartition())
                        .replicas(clickEventWindowTopicProp.getReplicationFactor())
                        .build(),
                TopicBuilder.name(pageClickEventWindowTopicProp.getTopic())
                        .partitions(pageClickEventWindowTopicProp.getPartition())
                        .replicas(pageClickEventWindowTopicProp.getReplicationFactor())
                        .build()
        );
    }
}
