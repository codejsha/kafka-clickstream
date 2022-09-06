package com.example.demo.util;

import com.example.demo.config.condition.ClickEventWindowEnabledCondition;
import com.example.demo.config.condition.PageClickEventWindowEnabledCondition;
import com.example.demo.config.condition.UserClickEventEnabledCondition;
import com.example.demo.config.condition.UserClickEventWindowEnabledCondition;
import com.google.common.collect.Maps;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.stereotype.Component;

@Component
public class StreamSerde {
    private static <T extends SpecificRecord> SpecificAvroSerde<T> configureSerde(
            KafkaStreamsConfiguration kafkaStreamsConfig, Class<T> record) {
        var isKeySerde = false;
        var serde = new SpecificAvroSerde<T>();
        var props = kafkaStreamsConfig.asProperties();
        var flattenedProps = PropertiesUtil.flattenProperties(props);
        var propsMap = Maps.fromProperties(flattenedProps);
        serde.configure(propsMap, isKeySerde);
        return serde;
    }

    @Component
    @Conditional(UserClickEventEnabledCondition.class)
    @RequiredArgsConstructor
    public static class UserClickEventSerde {
        @Qualifier("userClickEventKafkaStreamsConfig")
        private final KafkaStreamsConfiguration streamsConfig;

        public <T extends SpecificRecord> Serde<T> valueSerde(Class<T> record) {
            return configureSerde(streamsConfig, record);
        }
    }

    @Component
    @Conditional(UserClickEventWindowEnabledCondition.class)
    @RequiredArgsConstructor
    public static class UserClickEventWindowSerde {
        @Qualifier("userClickEventWindowKafkaStreamsConfig")
        private final KafkaStreamsConfiguration streamsConfig;

        public <T extends SpecificRecord> Serde<T> valueSerde(Class<T> record) {
            return configureSerde(streamsConfig, record);
        }
    }

    @Component
    @Conditional(ClickEventWindowEnabledCondition.class)
    @RequiredArgsConstructor
    public static class ClickEventWindowSerde {
        @Qualifier("clickEventWindowKafkaStreamsConfig")
        private final KafkaStreamsConfiguration streamsConfig;

        public <T extends SpecificRecord> Serde<T> recordSerde(Class<T> record) {
            return configureSerde(streamsConfig, record);
        }
    }

    @Component
    @Conditional(PageClickEventWindowEnabledCondition.class)
    @RequiredArgsConstructor
    public static class PageClickEventWindowSerde {
        @Qualifier("pageClickEventWindowKafkaStreamsConfig")
        private final KafkaStreamsConfiguration streamsConfig;

        public <T extends SpecificRecord> Serde<T> recordSerde(Class<T> record) {
            return configureSerde(streamsConfig, record);
        }
    }
}
