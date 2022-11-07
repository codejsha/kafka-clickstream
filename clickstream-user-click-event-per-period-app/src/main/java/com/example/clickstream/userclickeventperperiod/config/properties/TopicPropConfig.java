package com.example.clickstream.userclickeventperperiod.config.properties;

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
public class TopicPropConfig {
    @NestedConfigurationProperty
    private final TopicProp userClickEvent;
    @NestedConfigurationProperty
    private final TopicProp userClickEventPerPeriod;
}
