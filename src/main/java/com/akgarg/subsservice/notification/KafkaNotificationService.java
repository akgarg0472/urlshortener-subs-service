package com.akgarg.subsservice.notification;

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
@Profile("prod")
public class KafkaNotificationService implements NotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.notification.email.topic.name:urlshortener.notifications.email}")
    private String notificationTopicName;

    // TODO: fix method
    @Override
    public void sendSubscriptionSuccess(final String requestId, final SubscriptionDTO subscriptionDTO) {
        final var notificationEvent = new SubscriptionNotificationEvent();
        notificationEvent.setEventType(SubscriptionEventType.SUBSCRIPTION_SUCCESS);
        notificationEvent.setSubscription(subscriptionDTO);
        sendEvent(requestId, notificationEvent);
    }

    private void sendEvent(final String requestId, final SubscriptionNotificationEvent subscriptionNotificationEvent) {
        serializeEvent(requestId, subscriptionNotificationEvent)
                .ifPresent(json -> kafkaTemplate.send(notificationTopicName, json)
                        .whenComplete((result, throwable) -> {
                            if (throwable != null) {
                                log.error("[{}] Failed to send subscription event", requestId, throwable);
                            } else {
                                log.info("[{}] Successfully sent subscription event", requestId);
                            }
                        })
                );
    }

    private Optional<String> serializeEvent(final String requestId, final SubscriptionNotificationEvent subscriptionNotificationEvent) {
        try {
            return Optional.of(objectMapper.writeValueAsString(subscriptionNotificationEvent));
        } catch (Exception e) {
            log.error("[{}] Error serializing subscription notification event", requestId, e);
            return Optional.empty();
        }
    }

}
