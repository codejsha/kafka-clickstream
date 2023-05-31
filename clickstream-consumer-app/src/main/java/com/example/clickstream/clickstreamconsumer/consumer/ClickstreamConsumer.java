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
        var ip = record.get("ip").toString();
        var userid = Integer.parseInt(record.get("userid").toString());
        var remote_user = record.get("remote_user").toString();
        var time = record.get("time").toString();
        var time2 = Long.parseLong(record.get("_time").toString());
        var request = record.get("request").toString();
        var status = record.get("status").toString();
        var bytes = record.get("bytes").toString();
        var referrer = record.get("referrer").toString();
        var agent = record.get("agent").toString();
        var clickstream = new Event(ip, userid, remote_user, time, time2, request, status, bytes, referrer, agent);

        log.info("Received clickstream event: {} | {}", key, clickstream);
    }
}
