# kafka-clickstream

## Overview

This project showcases Kafka Streams applications that process datagen clickstream data. The demo is based on the clickstream tutorial (https://github.com/confluentinc/examples). The *clickstream* data is generated by the [Datagen Source Connector](https://github.com/confluentinc/kafka-connect-datagen).

The demo is composed of the following applications:

- clickstream-streams-app: A Kafka Streams application that processes the clickstream data and writes the results to a Kafka topic. The application is written in Java and uses the [Spring for Apache Kafka](https://spring.io/projects/spring-kafka).
