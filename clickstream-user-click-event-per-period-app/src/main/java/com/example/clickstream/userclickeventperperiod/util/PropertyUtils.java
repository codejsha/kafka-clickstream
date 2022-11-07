package com.example.clickstream.userclickeventperperiod.util;

import com.google.common.collect.Maps;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.Map;
import java.util.Properties;

public class PropertyUtils {
    public static Map<String, Object> configBase(KafkaProperties properties) {
        var consumerConfigMap = properties.buildConsumerProperties();
        var producerConfigMap = properties.buildProducerProperties();
        var adminConfigMap = properties.buildAdminProperties();
        var streamsConfigMap = properties.buildStreamsProperties();

        var configMap = Maps.<String, Object>newHashMap();
        configMap.putAll(consumerConfigMap);
        configMap.putAll(producerConfigMap);
        configMap.putAll(adminConfigMap);
        configMap.putAll(streamsConfigMap);

        return configMap;
    }

    public static Map<String, Object> envMapToConfigMap(Map<String, String> envMap) {
        var configMap = Maps.<String, Object>newHashMap();
        envMap.entrySet().stream()
                .map(entry -> {
                    var configParam = entry.getKey()
                            .replace("_", ".")
                            .toLowerCase()
                            .replaceFirst("^kafka.", "");
                    return Map.entry(configParam, entry.getValue());
                })
                .forEach(entry -> configMap.put(entry.getKey(), entry.getValue()));
        return configMap;
    }

    public static Properties flattenProperties(Properties properties) {
        for (var entry : properties.entrySet()) {
            var key = entry.getKey().toString();
            var value = entry.getValue().toString().replaceAll("[\\[\\]\\s]", "");
            properties.put(key, value);
        }
        return properties;
    }
}
