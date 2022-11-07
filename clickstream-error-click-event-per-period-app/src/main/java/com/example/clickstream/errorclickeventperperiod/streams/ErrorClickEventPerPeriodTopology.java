package com.example.clickstream.errorclickeventperperiod.streams;

import clickstream.Event;
import com.example.clickstream.errorclickeventperperiod.config.properties.TopicProp;
import com.example.clickstream.errorclickeventperperiod.config.properties.TopicPropConfig;
import com.example.clickstream.errorclickeventperperiod.util.SerdeUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ErrorClickEventPerPeriodTopology {
    private static final Duration windowSize = Duration.ofSeconds(60);
    private static final Duration advance = Duration.ofSeconds(20);
    private final TopicProp sourceTopic;
    private final TopicProp sinkTopic;
    private final SerdeUtils.StreamSerde streamSerde;

    public ErrorClickEventPerPeriodTopology(TopicPropConfig topicPropConfig,
                                            SerdeUtils.StreamSerde streamSerde) {
        this.sourceTopic = topicPropConfig.getEvent();
        this.sinkTopic = topicPropConfig.getErrorClickEventPerPeriod();
        this.streamSerde = streamSerde;
    }

    @Bean
    public KTable<Windowed<String>, Long> errorClickEventHoppingWindowProcess(StreamsBuilder streamsBuilder) {
        var table = streamsBuilder
                .stream(sourceTopic.getTopic(),
                        Consumed.with(Serdes.String(), streamSerde.valueSerde(Event.class))
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .filter((k, v) -> Integer.parseInt(v.getStatus()) > 400)
                .groupBy((k, v) -> v.getStatus(),
                        Grouped.with(Serdes.String(), streamSerde.valueSerde(Event.class)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(windowSize).advanceBy(advance))
                .count();

        table.toStream().to(sinkTopic.getTopic(),
                Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class, windowSize.toMillis()), Serdes.Long()));

        return table;
    }
}
