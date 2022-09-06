package com.example.demo.config;

import com.example.demo.config.properties.TopicConfigProp;
import com.example.demo.config.properties.TopicProp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTopicPropFactory {
    private final TopicConfigProp topicConfigProp;

    @Bean
    public TopicProp clickstreamTopicProp() {
        return topicConfigProp.getClickstream();
    }

    @Bean
    public TopicProp clickstreamUserTopicProp() {
        return topicConfigProp.getClickstreamUser();
    }

    @Bean
    public TopicProp clickstreamCodeTopicProp() {
        return topicConfigProp.getClickstreamCode();
    }

    @Bean
    public TopicProp userClickEventTopicProp() {
        return topicConfigProp.getUserClickEvent();
    }

    @Bean
    public TopicProp userClickEventWindowTopicProp() {
        return topicConfigProp.getUserClickEventWindow();
    }

    @Bean
    public TopicProp clickEventWindowTopicProp() {
        return topicConfigProp.getClickEventWindow();
    }

    @Bean
    public TopicProp pageClickEventWindowTopicProp() {
        return topicConfigProp.getPageClickEventWindow();
    }
}
