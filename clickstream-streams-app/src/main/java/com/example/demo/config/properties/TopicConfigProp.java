package com.example.demo.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("app.topics")
@ConstructorBinding
@Getter
@ToString
@RequiredArgsConstructor
public class TopicConfigProp {
    @NestedConfigurationProperty
    private final TopicProp clickstream;
    @NestedConfigurationProperty
    private final TopicProp clickstreamUser;
    @NestedConfigurationProperty
    private final TopicProp clickstreamCode;
    @NestedConfigurationProperty
    private final TopicProp userClickEvent;
    @NestedConfigurationProperty
    private final TopicProp userClickEventWindow;
    @NestedConfigurationProperty
    private final TopicProp clickEventWindow;
    @NestedConfigurationProperty
    private final TopicProp pageClickEventWindow;
}
