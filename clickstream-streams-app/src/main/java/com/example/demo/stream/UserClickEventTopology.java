package com.example.demo.stream;

import clickstream.Event;
import clickstream.User;
import clickstream.UserClickEvent;
import com.example.demo.config.condition.UserClickEventEnabledCondition;
import com.example.demo.config.properties.TopicProp;
import com.example.demo.config.properties.TopologyProp;
import com.example.demo.joiner.UserClickEventJoiner;
import com.example.demo.util.StreamSerde;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(UserClickEventEnabledCondition.class)
@RequiredArgsConstructor
public class UserClickEventTopology {
    @Qualifier("clickstreamTopicProp")
    private final TopicProp clickTopic;
    @Qualifier("clickstreamUserTopicProp")
    private final TopicProp userTopic;
    @Qualifier("userClickEventTopicProp")
    private final TopicProp sinkTopic;
    @Qualifier("userClickEventTopologyProp")
    private final TopologyProp topologyProp;
    private final StreamSerde.UserClickEventSerde streamSerde;
    private final UserClickEventJoiner valueJoiner;

    @Bean
    public KStream<String, UserClickEvent> userClickEventProcess(
            @Qualifier("userClickEventStreamBuilder") StreamsBuilder streamsBuilder) {
        var eventStream = streamsBuilder
                .stream(clickTopic.getTopic(),
                        Consumed.with(Serdes.String(), streamSerde.valueSerde(Event.class))
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .map((k, v) -> KeyValue.pair(String.valueOf(v.getUserid()), v));

        var userTable = streamsBuilder
                .globalTable(userTopic.getTopic(),
                        Consumed.with(Serdes.String(), streamSerde.valueSerde(User.class))
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST),
                        Materialized
                                .<String, User, KeyValueStore<Bytes, byte[]>>
                                        as(topologyProp.getStoreName())
                                .withKeySerde(Serdes.String())
                                .withValueSerde(streamSerde.valueSerde(User.class)));

        return eventStream.join(userTable, (key, value) -> key, valueJoiner);
    }

    @Bean
    public KStream<String, UserClickEvent> userClickEventSink(
            @Qualifier("userClickEventProcess") KStream<String, UserClickEvent> stream) {
        stream.to(sinkTopic.getTopic(),
                Produced.with(Serdes.String(), streamSerde.valueSerde(UserClickEvent.class)));
        return stream;
    }
}
