package com.example.clickstream.clickeventperperiod.util;

import com.google.common.collect.Maps;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.stereotype.Component;

@Component
public class SerdeUtils {
    private static <T extends SpecificRecord> SpecificAvroSerde<T> configureSerde(
            KafkaStreamsConfiguration kafkaStreamsConfig, Class<T> record) {
        var isKeySerde = false;
        var serde = new SpecificAvroSerde<T>();
        var props = kafkaStreamsConfig.asProperties();
        var flattenedProps = PropertyUtils.flattenProperties(props);
        var propsMap = Maps.fromProperties(flattenedProps);
        serde.configure(propsMap, isKeySerde);
        return serde;
    }

    @Component
    @RequiredArgsConstructor
    public static class StreamSerde {
        private final KafkaStreamsConfiguration streamsConfig;

        public <T extends SpecificRecord> Serde<T> valueSerde(Class<T> record) {
            return configureSerde(streamsConfig, record);
        }
    }
}
