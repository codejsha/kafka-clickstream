package com.example.demo.config;

import com.example.demo.config.properties.TopologyConfigProp;
import com.example.demo.config.properties.TopologyProp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaStreamsTopologyPropFactory {
    private final TopologyConfigProp topologyConfigProp;

    @Bean
    public TopologyProp userClickEventTopologyProp() {
        return topologyConfigProp.getUserClickEvent();
    }

    @Bean
    public TopologyProp userClickEventWindowTopologyProp() {
        return topologyConfigProp.getUserClickEventWindow();
    }

    @Bean
    public TopologyProp clickEventWindowTopologyProp() {
        return topologyConfigProp.getClickEventWindow();
    }

    @Bean
    public TopologyProp pageClickEventWindowTopologyProp() {
        return topologyConfigProp.getPageClickEventWindow();
    }
}
