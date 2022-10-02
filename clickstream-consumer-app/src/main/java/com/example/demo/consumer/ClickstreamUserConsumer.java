package com.example.demo.consumer;

import clickstream.Code;
import clickstream.Event;
import clickstream.User;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@ConditionalOnProperty(prefix = "app.consumer", name = "type", havingValue = "clickstream-user")
@Component
@Slf4j
public class ClickstreamUserConsumer {
    @KafkaListener(topics = "${app.topics.clickstream-user.topic}")
    public void receiveClickstreamUserMessage(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                              @Header(KafkaHeaders.RECEIVED_PARTITION_ID) String partition,
                                              @Header(KafkaHeaders.OFFSET) int offset,
                                              @Payload GenericRecord record) {
        var userid = Integer.parseInt(record.get("user_id").toString());
        var username = record.get("username").toString();
        var registered_at = Long.parseLong(record.get("registered_at").toString());
        var firstname = record.get("first_name").toString();
        var lastname = record.get("last_name").toString();
        var city = record.get("city").toString();
        var level = record.get("level").toString();
        var clickstreamUser = new User(userid, username, registered_at, firstname, lastname, city, level);

        log.info("Received clickstream user: {} | {}", key, clickstreamUser);
    }
}
