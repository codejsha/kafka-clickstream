package com.example.demo.config;

import com.example.demo.config.condition.ClickEventWindowEnabledCondition;
import com.example.demo.config.condition.PageClickEventWindowEnabledCondition;
import com.example.demo.config.condition.UserClickEventEnabledCondition;
import com.example.demo.config.condition.UserClickEventWindowEnabledCondition;
import com.example.demo.config.properties.TopologyConfigProp;
import com.example.demo.util.PropertiesUtil;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

@Configuration
@EnableKafka
// @EnableKafkaStreams
@RequiredArgsConstructor
public class KafkaStreamsBuilderConfig {
    private final TopologyConfigProp topologyConfigProp;

    /* DEFAULT_STREAMS_CONFIG_BEAN_NAME */

    // @Primary
    // @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    // public KafkaStreamsConfiguration myKafkaStreamsConfig(KafkaProperties properties) {
    //     var configMap = baseConfig(properties);
    //     return new KafkaStreamsConfiguration(configMap);
    // }

    /* user click event */

    @Bean
    @Conditional(UserClickEventEnabledCondition.class)
    public KafkaStreamsConfiguration userClickEventKafkaStreamsConfig(KafkaProperties properties) {
        var configMap = PropertiesUtil.configBase(properties);
        configMap.put(StreamsConfig.APPLICATION_ID_CONFIG, topologyConfigProp.getUserClickEvent().getApplicationId());
        configMap.put(StreamsConfig.CLIENT_ID_CONFIG, topologyConfigProp.getUserClickEvent().getClientId());
        return new KafkaStreamsConfiguration(configMap);
    }

    @Bean
    @Conditional(UserClickEventEnabledCondition.class)
    public FactoryBean<StreamsBuilder> userClickEventStreamBuilder(
            @Qualifier("userClickEventKafkaStreamsConfig") KafkaStreamsConfiguration streamsConfig) {
        return new StreamsBuilderFactoryBean(streamsConfig);
    }

    /* user click event window */

    @Bean
    @Conditional(UserClickEventWindowEnabledCondition.class)
    public KafkaStreamsConfiguration userClickEventWindowKafkaStreamsConfig(KafkaProperties properties) {
        var configMap = PropertiesUtil.configBase(properties);
        configMap.put(StreamsConfig.APPLICATION_ID_CONFIG, topologyConfigProp.getUserClickEventWindow().getApplicationId());
        configMap.put(StreamsConfig.CLIENT_ID_CONFIG, topologyConfigProp.getUserClickEventWindow().getClientId());
        return new KafkaStreamsConfiguration(configMap);
    }

    @Bean
    @Conditional(UserClickEventWindowEnabledCondition.class)
    public FactoryBean<StreamsBuilder> userClickEventWindowStreamBuilder(
            @Qualifier("userClickEventWindowKafkaStreamsConfig") KafkaStreamsConfiguration streamsConfig) {
        return new StreamsBuilderFactoryBean(streamsConfig);
    }

    /* click event window */

    @Bean
    @Conditional(ClickEventWindowEnabledCondition.class)
    public KafkaStreamsConfiguration clickEventWindowKafkaStreamsConfig(KafkaProperties properties) {
        var configMap = PropertiesUtil.configBase(properties);
        configMap.put(StreamsConfig.APPLICATION_ID_CONFIG, topologyConfigProp.getClickEventWindow().getApplicationId());
        configMap.put(StreamsConfig.CLIENT_ID_CONFIG, topologyConfigProp.getClickEventWindow().getClientId());
        return new KafkaStreamsConfiguration(configMap);
    }

    @Bean
    @Conditional(ClickEventWindowEnabledCondition.class)
    public FactoryBean<StreamsBuilder> clickEventWindowStreamBuilder(
            @Qualifier("clickEventWindowKafkaStreamsConfig") KafkaStreamsConfiguration streamsConfig) {
        return new StreamsBuilderFactoryBean(streamsConfig);
    }

    /* page click event window */

    @Bean
    @Conditional(PageClickEventWindowEnabledCondition.class)
    public KafkaStreamsConfiguration pageClickEventWindowKafkaStreamsConfig(KafkaProperties properties) {
        var configMap = PropertiesUtil.configBase(properties);
        configMap.put(StreamsConfig.APPLICATION_ID_CONFIG, topologyConfigProp.getPageClickEventWindow().getApplicationId());
        configMap.put(StreamsConfig.CLIENT_ID_CONFIG, topologyConfigProp.getPageClickEventWindow().getClientId());
        return new KafkaStreamsConfiguration(configMap);
    }

    @Bean
    @Conditional(PageClickEventWindowEnabledCondition.class)
    public FactoryBean<StreamsBuilder> pageClickEventWindowStreamBuilder(
            @Qualifier("pageClickEventWindowKafkaStreamsConfig") KafkaStreamsConfiguration streamsConfig) {
        return new StreamsBuilderFactoryBean(streamsConfig);
    }
}
