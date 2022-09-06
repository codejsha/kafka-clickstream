package com.example.demo.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("app.topology")
@ConstructorBinding
@Getter
@ToString
@RequiredArgsConstructor
public class TopologyConfigProp {
    @NestedConfigurationProperty
    private final TopologyProp userClickEvent;
    @NestedConfigurationProperty
    private final TopologyProp userClickEventWindow;
    @NestedConfigurationProperty
    private final TopologyProp clickEventWindow;
    @NestedConfigurationProperty
    private final TopologyProp pageClickEventWindow;
}
