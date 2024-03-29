package com.example.clickstream.clickstreamconsumer.consumer;

import clickstream.Code;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix = "app.consumer", name = "type", havingValue = "clickstream-code")
@Component
@Slf4j
public class ClickstreamCodeConsumer {
    @KafkaListener(topics = "${app.topics.clickstream-code.topic}")
    public void receiveClickstreamCodeMessage(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                              @Header(KafkaHeaders.RECEIVED_PARTITION_ID) String partition,
                                              @Header(KafkaHeaders.OFFSET) int offset,
                                              @Payload GenericRecord record) {
        var clickstreamCode = Code.newBuilder()
                .setCode(Integer.parseInt(record.get("code").toString()))
                .setDefinition(record.get("definition").toString())
                .build();
        log.info("Received clickstream code: {} | {}", key, clickstreamCode);
    }
}
