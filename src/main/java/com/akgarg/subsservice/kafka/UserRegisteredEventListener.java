package com.akgarg.subsservice.kafka;

import com.akgarg.subsservice.v1.subs.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static com.akgarg.subsservice.utils.SubsUtils.REQUEST_ID_THREAD_CONTEXT_KEY;

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
        ThreadContext.put(REQUEST_ID_THREAD_CONTEXT_KEY, requestId);

        log.info("Received kafka user registration event: {}", consumerRecord.value());

        try {
            final var registerCompleteEvent = objectMapper.readValue(consumerRecord.value(), RegisterCompleteEvent.class);
            subscriptionService.subscribeDefaultPack(registerCompleteEvent.userId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing user registration event", e);
        }
    }

}
