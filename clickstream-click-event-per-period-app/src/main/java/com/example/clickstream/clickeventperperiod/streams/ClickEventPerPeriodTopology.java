package com.example.clickstream.clickeventperperiod.streams;

import clickstream.Event;
import clickstream.IdCountWindow;
import com.example.clickstream.clickeventperperiod.config.properties.TopicProp;
import com.example.clickstream.clickeventperperiod.config.properties.TopicPropConfig;
import com.example.clickstream.clickeventperperiod.util.SerdeUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ClickEventPerPeriodTopology {
    private static final Duration windowSize = Duration.ofSeconds(60);
    private final TopicProp sourceTopic;
    private final TopicProp sinkTopic;
    private final SerdeUtils.StreamSerde streamSerde;

    public ClickEventPerPeriodTopology(TopicPropConfig topicPropConfig,
                                       SerdeUtils.StreamSerde streamSerde) {
        this.sourceTopic = topicPropConfig.getEvent();
        this.sinkTopic = topicPropConfig.getClickEventPerPeriod();
        this.streamSerde = streamSerde;
    }

    @Bean
    public KTable<Windowed<Integer>, Long> clickEventTumblingWindowProcess(StreamsBuilder streamsBuilder) {
        var table = streamsBuilder
                .stream(sourceTopic.getTopic(),
                        Consumed.with(Serdes.String(), streamSerde.valueSerde(Event.class))
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .mapValues(e -> IdCountWindow.newBuilder()
                        .setUserid(e.getUserid())
                        .setWindowStart(1L)
                        .setWindowStop(1L)
                        .setCount(1L)
                        .build())
                .groupBy((k, v) -> v.getUserid(),
                        Grouped.with(Serdes.Integer(), streamSerde.valueSerde(IdCountWindow.class)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(windowSize))
                .count();

        table.toStream().to(sinkTopic.getTopic(),
                Produced.with(WindowedSerdes.timeWindowedSerdeFrom(Integer.class, windowSize.toMillis()), Serdes.Long()));

        return table;
    }
}
