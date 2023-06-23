package com.example.clickstream.clickstreamconsumer.consumer;

import clickstream.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix = "app.consumer", name = "type", havingValue = "clickstream")
@Component
@Slf4j
public class ClickstreamConsumer {
    @KafkaListener(topics = "${app.topics.clickstream.topic}")
    public void receiveClickstreamEventMessage(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                               @Header(KafkaHeaders.RECEIVED_PARTITION_ID) String partition,
                                               @Header(KafkaHeaders.OFFSET) int offset,
                                               @Payload GenericRecord record) {
        var clickstreamEvent = Event.newBuilder()
                .setIp(record.get("ip").toString())
                .setUserid(Integer.parseInt(record.get("userid").toString()))
                .setRemoteUser(record.get("remote_user").toString())
                .setTime(record.get("time").toString())
                .setTime$1(Long.parseLong(record.get("_time").toString()))
                .setRequest(record.get("request").toString())
                .setStatus(record.get("status").toString())
                .setBytes(record.get("bytes").toString())
                .setReferrer(record.get("referrer").toString())
                .setAgent(record.get("agent").toString())
                .build();
        log.info("Received clickstream event: {} | {}", key, clickstreamEvent);
    }
}
