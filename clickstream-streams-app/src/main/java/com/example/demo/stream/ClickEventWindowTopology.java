package com.example.demo.stream;

import clickstream.Event;
import clickstream.IdCountWindow;
import com.example.demo.config.condition.ClickEventWindowEnabledCondition;
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
@Conditional(ClickEventWindowEnabledCondition.class)
@RequiredArgsConstructor
public class ClickEventWindowTopology {
    private static final Duration windowSize = Duration.ofSeconds(60);
    @Qualifier("clickstreamTopicProp")
    private final TopicProp clickTopic;
    @Qualifier("clickEventWindowTopicProp")
    private final TopicProp sinkTopic;
    private final StreamSerde.ClickEventWindowSerde streamSerde;

    @Bean
    public KTable<Windowed<Integer>, Long> clickEventTumblingWindowProcess(
            @Qualifier("clickEventWindowStreamBuilder") StreamsBuilder streamsBuilder) {
        return streamsBuilder
                .stream(clickTopic.getTopic(),
                        Consumed.with(Serdes.String(), streamSerde.recordSerde(Event.class))
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .mapValues(e -> IdCountWindow.newBuilder()
                        .setUserid(e.getUserid())
                        .setWindowStart(1L)
                        .setWindowStop(1L)
                        .setCount(1L)
                        .build())
                .groupBy((k, v) -> v.getUserid(),
                        Grouped.with(Serdes.Integer(), streamSerde.recordSerde(IdCountWindow.class)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(windowSize))
                .count();
    }

    @Bean
    public KTable<Windowed<Integer>, Long> clickEventWindowSink(
            @Qualifier("clickEventTumblingWindowProcess") KTable<Windowed<Integer>, Long> table) {
        table.toStream().to(sinkTopic.getTopic(),
                Produced.with(WindowedSerdes.timeWindowedSerdeFrom(Integer.class, windowSize.toMillis()), Serdes.Long()));
        return table;
    }
}
