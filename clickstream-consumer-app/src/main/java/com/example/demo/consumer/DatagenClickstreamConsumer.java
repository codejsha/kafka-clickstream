package com.example.demo.consumer;

import clickstream.Code;
import clickstream.Event;
import clickstream.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatagenClickstreamConsumer {

    @KafkaListener(topics = "${app.topics.clickstream.name}")
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

    @KafkaListener(topics = "${app.topics.clickstream-codes.name}")
    public void receiveClickstreamCodeMessage(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                              @Header(KafkaHeaders.RECEIVED_PARTITION_ID) String partition,
                                              @Header(KafkaHeaders.OFFSET) int offset,
                                              @Payload GenericRecord record) {
        var code = Integer.parseInt(record.get("code").toString());
        var definition = record.get("definition").toString();
        var clickstreamCode = new Code(code, definition);

        log.info("Received clickstream code: {} | {}", key, clickstreamCode);
    }

    @KafkaListener(topics = "${app.topics.clickstream-users.name}")
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
