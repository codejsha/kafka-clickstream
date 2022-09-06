package com.example.demo.config;

import com.example.demo.config.properties.TopicConfigProp;
import com.example.demo.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@EnableConfigurationProperties(value = {TopicConfigProp.class})
@Slf4j
public class KafkaTopicTest {
    private static final Network network = Network.newNetwork();
    @Container
    private final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"))
            .withNetwork(network)
            .withNetworkAliases("kafka")
            .withExposedPorts(9093)
            .waitingFor(Wait.forListeningPort())
            .waitingFor(Wait.forLogMessage(".*started.*", 1));
    @Container
    private final GenericContainer<?> schemaRegistryContainer = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-schema-registry:7.2.1"))
            .withNetwork(network)
            .withNetworkAliases("schema-registry")
            .withExposedPorts(8081)
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
            .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka:9092")
            .dependsOn(kafkaContainer)
            .waitingFor(Wait.forListeningPort())
            .waitingFor(Wait.forHttp("/subjects").forStatusCode(200));
    @Autowired
    private TopicConfigProp topicConfigProp;
    private AdminClient adminClient;
    private KafkaAdmin kafkaAdmin;

    @BeforeEach
    public void startContainers() {
        Startables.deepStart(Stream.of(kafkaContainer, schemaRegistryContainer)).join();

        var envMap = kafkaContainer.getEnvMap();
        envMap.put("KAFKA_BOOTSTRAP_SERVERS", kafkaContainer.getBootstrapServers());
        adminClient = AdminClient.create(PropertiesUtil.envMapToConfigMap(envMap));
        kafkaAdmin = new KafkaAdmin(PropertiesUtil.envMapToConfigMap(envMap));
    }

    @AfterEach
    public void stopContainers() {
        adminClient.close();
        kafkaAdmin = null;
    }

    @Test
    public void testTopicCreation() throws ExecutionException, InterruptedException {
        var topic1 = Collections.singletonList(
                new NewTopic(topicConfigProp.getClickstream().getTopic(),
                        topicConfigProp.getClickstream().getPartition(),
                        (short) topicConfigProp.getClickstream().getReplicationFactor()));
        var topic2 = Collections.singletonList(
                new NewTopic(topicConfigProp.getClickstreamUser().getTopic(),
                        topicConfigProp.getClickstreamUser().getPartition(),
                        (short) topicConfigProp.getClickstreamUser().getReplicationFactor()));
        var topic3 = Collections.singletonList(
                new NewTopic(topicConfigProp.getClickstreamCode().getTopic(),
                        topicConfigProp.getClickstreamCode().getPartition(),
                        (short) topicConfigProp.getClickstreamCode().getReplicationFactor()));
        adminClient.createTopics(topic1);
        adminClient.createTopics(topic2);
        adminClient.createTopics(topic3);

        var result =
                adminClient.listTopics()
                        .names()
                        .get();
        log.info("topic list: {}", result);

        assert result.contains(topicConfigProp.getClickstream().getTopic());
        assert result.contains(topicConfigProp.getClickstreamUser().getTopic());
        assert result.contains(topicConfigProp.getClickstreamCode().getTopic());
    }

    @Test
    public void testTopicCreation2() throws ExecutionException, InterruptedException {
        kafkaAdmin.createOrModifyTopics(
                TopicBuilder.name(topicConfigProp.getClickstream().getTopic())
                        .partitions(topicConfigProp.getClickstream().getPartition())
                        .replicas(topicConfigProp.getClickstream().getReplicationFactor())
                        .build(),
                TopicBuilder.name(topicConfigProp.getClickstreamUser().getTopic())
                        .partitions(topicConfigProp.getClickstreamUser().getPartition())
                        .replicas(topicConfigProp.getClickstreamUser().getReplicationFactor())
                        .build(),
                TopicBuilder.name(topicConfigProp.getClickstreamCode().getTopic())
                        .partitions(topicConfigProp.getClickstreamCode().getPartition())
                        .replicas(topicConfigProp.getClickstreamCode().getReplicationFactor())
                        .build());

        var result =
                adminClient.listTopics()
                        .names()
                        .get();
        log.info("topic list: {}", result);

        assert result.contains(topicConfigProp.getClickstream().getTopic());
        assert result.contains(topicConfigProp.getClickstreamUser().getTopic());
        assert result.contains(topicConfigProp.getClickstreamCode().getTopic());
    }
}
