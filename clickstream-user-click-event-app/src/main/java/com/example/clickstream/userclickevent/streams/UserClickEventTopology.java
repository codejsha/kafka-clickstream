package com.example.clickstream.userclickevent.streams;

import clickstream.Event;
import clickstream.User;
import clickstream.UserClickEvent;
import com.example.clickstream.userclickevent.config.properties.TopicProp;
import com.example.clickstream.userclickevent.config.properties.TopicPropConfig;
import com.example.clickstream.userclickevent.joiner.UserClickEventJoiner;
import com.example.clickstream.userclickevent.util.SerdeUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UserClickEventTopology {
    private final TopicProp eventTopic;
    private final TopicProp userTopic;
    private final TopicProp sinkTopic;
    private final SerdeUtils.StreamSerde streamSerde;
    private final UserClickEventJoiner valueJoiner;

    public UserClickEventTopology(TopicPropConfig topicPropConfig,
                                  SerdeUtils.StreamSerde streamSerde,
                                  UserClickEventJoiner valueJoiner) {
        this.eventTopic = topicPropConfig.getEvent();
        this.userTopic = topicPropConfig.getUser();
        this.sinkTopic = topicPropConfig.getUserClickEvent();
        this.streamSerde = streamSerde;
        this.valueJoiner = valueJoiner;
    }

    @Bean
    public KStream<String, UserClickEvent> userClickEventProcess(StreamsBuilder streamsBuilder) {
        var eventStream = streamsBuilder
                .stream(eventTopic.getTopic(),
                        Consumed.with(Serdes.String(), streamSerde.valueSerde(Event.class))
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .map((k, v) -> KeyValue.pair(String.valueOf(v.getUserid()), v));

        var userTable = streamsBuilder
                .globalTable(userTopic.getTopic(),
                        Consumed.with(Serdes.String(), streamSerde.valueSerde(User.class))
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST),
                        Materialized
                                .<String, User, KeyValueStore<Bytes, byte[]>>
                                        as(sinkTopic.getStore())
                                .withKeySerde(Serdes.String())
                                .withValueSerde(streamSerde.valueSerde(User.class)));

        var stream = eventStream.join(userTable, (key, value) -> key, valueJoiner);
        stream.to(sinkTopic.getTopic(),
                Produced.with(Serdes.String(), streamSerde.valueSerde(UserClickEvent.class)));

        return stream;
    }
}
