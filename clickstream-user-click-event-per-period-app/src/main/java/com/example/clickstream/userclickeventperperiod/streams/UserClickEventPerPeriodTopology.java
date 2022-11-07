package com.example.clickstream.userclickeventperperiod.streams;

import clickstream.UserClickEvent;
import com.example.clickstream.userclickeventperperiod.config.properties.TopicProp;
import com.example.clickstream.userclickeventperperiod.config.properties.TopicPropConfig;
import com.example.clickstream.userclickeventperperiod.util.SerdeUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class UserClickEventPerPeriodTopology {
    private static final Duration windowSize = Duration.ofSeconds(30);
    private final TopicProp sourceTopic;
    private final TopicProp sinkTopic;
    private final SerdeUtils.StreamSerde streamSerde;

    public UserClickEventPerPeriodTopology(TopicPropConfig topicPropConfig,
                                           SerdeUtils.StreamSerde streamSerde) {
        this.sourceTopic = topicPropConfig.getUserClickEvent();
        this.sinkTopic = topicPropConfig.getUserClickEventPerPeriod();
        this.streamSerde = streamSerde;
    }

    @Bean
    public KTable<Windowed<String>, Long> userClickEventSessionWindowProcess(StreamsBuilder streamsBuilder) {
        var table = streamsBuilder
                .stream(sourceTopic.getTopic(),
                        Consumed.with(Serdes.String(), streamSerde.valueSerde(UserClickEvent.class))
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .groupBy((k, v) -> v.getUsername(),
                        Grouped.with(Serdes.String(), streamSerde.valueSerde(UserClickEvent.class)))
                .windowedBy(SessionWindows.ofInactivityGapWithNoGrace(windowSize))
                .count();

        table.toStream().to(sinkTopic.getTopic(),
                Produced.with(WindowedSerdes.sessionWindowedSerdeFrom(String.class), Serdes.Long()));

        return table;
    }
}
