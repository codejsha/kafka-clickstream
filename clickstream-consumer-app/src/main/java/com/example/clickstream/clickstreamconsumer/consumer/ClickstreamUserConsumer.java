package com.example.clickstream.clickstreamconsumer.consumer;

import clickstream.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix = "app.consumer", name = "type", havingValue = "clickstream-user")
@Component
@Slf4j
public class ClickstreamUserConsumer {
    @KafkaListener(topics = "${app.topics.clickstream-user.topic}")
    public void receiveClickstreamUserMessage(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                              @Header(KafkaHeaders.RECEIVED_PARTITION_ID) String partition,
                                              @Header(KafkaHeaders.OFFSET) int offset,
                                              @Payload GenericRecord record) {
        var clickstreamUser = User.newBuilder()
                .setUserId(Integer.parseInt(record.get("user_id").toString()))
                .setUsername(record.get("username").toString())
                .setRegisteredAt(Long.parseLong(record.get("registered_at").toString()))
                .setFirstName(record.get("first_name").toString())
                .setLastName(record.get("last_name").toString())
                .setCity(record.get("city").toString())
                .setLevel(record.get("level").toString())
                .build();
        log.info("Received clickstream user: {} | {}", key, clickstreamUser);
    }
}
