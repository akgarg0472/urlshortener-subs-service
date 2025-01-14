package com.akgarg.subsservice.kafka;

import com.akgarg.subsservice.v1.subs.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener extends AbstractKafkaEventListener {

    private final SubscriptionService subscriptionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.user.registration.success.topic.name:user.registration.completed}",
            containerFactory = "userRegisteredEventManualAckConcurrentKafkaListenerContainerFactory")
    public void onMessage(final ConsumerRecord<String, String> consumerRecord, final Acknowledgment acknowledgment) {
        final var requestId = generateRequestId(consumerRecord);

        log.info("Received kafka user registration event: {}", consumerRecord.value());

        try {
            final var registerCompleteEvent = objectMapper.readValue(consumerRecord.value(), RegisterCompleteEvent.class);
            subscriptionService.subscribeDefaultPack(requestId, registerCompleteEvent.userId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("[{}] Error processing user registration event", requestId, e);
        }
    }

}
