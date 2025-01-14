package com.akgarg.subsservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.UUID;

public abstract class AbstractKafkaEventListener {

    String generateRequestId(final ConsumerRecord<?, ?> consumerRecord) {
        try {
            return String.format("kafka-%s-%d-%d", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset());
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

}
