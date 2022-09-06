package com.example.demo.stream;

import clickstream.UserClickEvent;
import com.example.demo.config.condition.UserClickEventWindowEnabledCondition;
import com.example.demo.config.properties.TopicProp;
import com.example.demo.util.StreamSerde;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Conditional(UserClickEventWindowEnabledCondition.class)
@RequiredArgsConstructor
public class UserClickEventWindowTopology {
    private static final Duration windowSize = Duration.ofSeconds(30);
    @Qualifier("userClickEventTopicProp")
    private final TopicProp userClickEventTopic;
    @Qualifier("userClickEventWindowTopicProp")
    private final TopicProp sinkTopic;
    private final StreamSerde.UserClickEventWindowSerde streamSerde;

    @Bean
    public KTable<Windowed<String>, Long> userClickEventSessionWindowProcess(
            @Qualifier("userClickEventWindowStreamBuilder") StreamsBuilder streamsBuilder) {
        return streamsBuilder
                .stream(userClickEventTopic.getTopic(),
                        Consumed.with(Serdes.String(), streamSerde.valueSerde(UserClickEvent.class))
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .groupBy((k, v) -> v.getUsername().toString(),
                        Grouped.with(Serdes.String(), streamSerde.valueSerde(UserClickEvent.class)))
                .windowedBy(SessionWindows.ofInactivityGapWithNoGrace(windowSize))
                .count();
    }

    @Bean
    public KTable<Windowed<String>, Long> userClickEventWindowSink(
            @Qualifier("userClickEventSessionWindowProcess") KTable<Windowed<String>, Long> table) {
        table.toStream().to(sinkTopic.getTopic(),
                Produced.with(WindowedSerdes.sessionWindowedSerdeFrom(String.class), Serdes.Long()));
        return table;
    }
}
