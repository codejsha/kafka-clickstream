package com.example.demo.stream;

import clickstream.Event;
import clickstream.IdCountWindow;
import com.example.demo.config.condition.PageClickEventWindowEnabledCondition;
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
@Conditional(PageClickEventWindowEnabledCondition.class)
@RequiredArgsConstructor
public class PageClickEventWindowTopology {
    private static final Duration windowSize = Duration.ofSeconds(60);
    private static final Duration advance = Duration.ofSeconds(5);
    @Qualifier("clickstreamTopicProp")
    private final TopicProp clickTopic;
    @Qualifier("pageClickEventWindowTopicProp")
    private final TopicProp sinkTopic;
    private final StreamSerde.PageClickEventWindowSerde streamSerde;

    @Bean
    public KTable<Windowed<Integer>, Long> pageClickEventHoppingWindowProcess(
            @Qualifier("pageClickEventWindowStreamBuilder") StreamsBuilder streamsBuilder) {
        return streamsBuilder
                .stream(clickTopic.getTopic(),
                        Consumed.with(Serdes.String(), streamSerde.recordSerde(Event.class))
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .filter((k, v) -> String.valueOf(v.getRequest()).contains("html"))
                .mapValues(e -> IdCountWindow.newBuilder()
                        .setUserid(e.getUserid())
                        .setWindowStart(1L)
                        .setWindowStop(1L)
                        .setCount(1L)
                        .build())
                .groupBy((k, v) -> v.getUserid(),
                        Grouped.with(Serdes.Integer(), streamSerde.recordSerde(IdCountWindow.class)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(windowSize).advanceBy(advance))
                .count();
    }

    @Bean
    public KTable<Windowed<Integer>, Long> pageClickEventWindowSink(
            @Qualifier("pageClickEventHoppingWindowProcess") KTable<Windowed<Integer>, Long> table) {
        table.toStream().to(sinkTopic.getTopic(),
                Produced.with(WindowedSerdes.timeWindowedSerdeFrom(Integer.class, windowSize.toMillis()), Serdes.Long()));
        return table;
    }
}
