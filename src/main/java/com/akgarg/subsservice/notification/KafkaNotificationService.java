package com.akgarg.subsservice.notification;

import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.akgarg.subsservice.v1.subs.SubscriptionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"prod", "PROD"})
public class KafkaNotificationService implements NotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.subscription.update.topic.name:urlshortener.subscription.events}")
    private String subscriptionTopicName;

    @Override
    public void sendSubscriptionSuccess(final String requestId, final SubscriptionDTO subscriptionDTO) {
        final var subscriptionEvent = new SubscriptionEvent();
        subscriptionEvent.setEventType(SubscriptionEventType.SUBSCRIPTION_SUCCESS);
        subscriptionEvent.setSubscription(subscriptionDTO);
        sendEvent(requestId, subscriptionEvent);
    }

    @Override
    public void sendSubscriptionPackCreated(final String requestId, final SubscriptionPackDTO subscriptionPack) {
        final var subscriptionEvent = new SubscriptionEvent();
        subscriptionEvent.setEventType(SubscriptionEventType.SUBSCRIPTION_PACK_CREATED);
        subscriptionEvent.setSubscriptionPack(subscriptionPack);
        sendEvent(requestId, subscriptionEvent);
    }

    @Override
    public void sendSubscriptionPackUpdated(final String requestId, final SubscriptionPackDTO subscriptionPack) {
        final var subscriptionEvent = new SubscriptionEvent();
        subscriptionEvent.setEventType(SubscriptionEventType.SUBSCRIPTION_PACK_UPDATED);
        subscriptionEvent.setSubscriptionPack(subscriptionPack);
        sendEvent(requestId, subscriptionEvent);
    }

    @Override
    public void sendSubscriptionPackDeleted(final String requestId, final SubscriptionPackDTO subscriptionPack) {
        final var subscriptionEvent = new SubscriptionEvent();
        subscriptionEvent.setEventType(SubscriptionEventType.SUBSCRIPTION_PACK_DELETED);
        subscriptionEvent.setSubscriptionPack(subscriptionPack);
        sendEvent(requestId, subscriptionEvent);
    }

    private void sendEvent(final String requestId, final SubscriptionEvent subscriptionEvent) {
        serializeEvent(requestId, subscriptionEvent)
                .ifPresent(json -> kafkaTemplate.send(subscriptionTopicName, json)
                        .whenComplete((result, throwable) -> {
                            if (throwable != null) {
                                log.error("[{}] Failed to send subscription event", requestId, throwable);
                            } else {
                                log.info("[{}] Successfully sent subscription event", requestId);
                            }
                        })
                );
    }

    private Optional<String> serializeEvent(final String requestId, final SubscriptionEvent subscriptionEvent) {
        try {
            return Optional.of(objectMapper.writeValueAsString(subscriptionEvent));
        } catch (Exception e) {
            log.error("[{}] Error serializing subscription event", requestId, e);
            return Optional.empty();
        }
    }

}
