package com.akgarg.subsservice.notification;

import com.akgarg.subsservice.utils.SubsUtils;
import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.akgarg.subsservice.v1.subs.SubscriptionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.akgarg.subsservice.utils.SubsUtils.REQUEST_ID_THREAD_CONTEXT_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("prod")
public class KafkaNotificationService implements NotificationService {

    private static final String DEFAULT_LOGO_URL = "https://res.cloudinary.com/dmdbqq7fp/bysb90sd8dsjst6ieeno.png";
    private static final String DEFAULT_DASHBOARD_URL = "http://localhost:3000/dashboard";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    @Value("${kafka.notification.email.topic.name:urlshortener.notifications.email}")
    private String notificationTopicName;

    @Override
    public void sendSubscriptionSuccessEmail(final SubscriptionDTO subscription, final SubscriptionPackDTO pack) {
        if (subscription.getEmail() == null || subscription.getEmail().isEmpty()) {
            log.info("Subscription email is null or empty");
            return;
        }

        final var notificationEvent = new SubscriptionNotificationEvent();
        notificationEvent.setNotificationType(NotificationType.EMAIL);
        notificationEvent.setHtml(true);
        notificationEvent.setSubject("Subscription Activated Successfully \uD83C\uDF89");
        notificationEvent.setRecipients(new String[]{subscription.getEmail()});
        notificationEvent.setBody(getEmailBody(subscription, pack));
        sendEvent(ThreadContext.get(REQUEST_ID_THREAD_CONTEXT_KEY), notificationEvent);
    }

    private void sendEvent(final String requestId, final SubscriptionNotificationEvent subscriptionNotificationEvent) {
        serializeEvent(subscriptionNotificationEvent)
                .ifPresent(json -> kafkaTemplate.send(notificationTopicName, json)
                        .whenComplete((result, throwable) -> {
                            if (throwable != null) {
                                log.error("[{}] Failed to send subscription event", requestId, throwable);
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("[{}] Successfully sent subscription event", requestId);
                                }
                            }
                        })
                );
    }

    private Optional<String> serializeEvent(final SubscriptionNotificationEvent subscriptionNotificationEvent) {
        try {
            return Optional.of(objectMapper.writeValueAsString(subscriptionNotificationEvent));
        } catch (Exception e) {
            log.error("Error serializing subscription notification event", e);
            return Optional.empty();
        }
    }

    private String getEmailBody(final SubscriptionDTO subscription, final SubscriptionPackDTO pack) {
        final var params = NotificationEmailParams.builder()
                .logoUrl(environment.getProperty("subscription.notification.default-logo-url", DEFAULT_LOGO_URL))
                .dashboardLink(environment.getProperty("ui.dashboard.url", DEFAULT_DASHBOARD_URL))
                .name(subscription.getName() != null ? subscription.getName() : "user")
                .subscriptionId(subscription.getId())
                .subscriptionPack(pack.name())
                .activeAt(SubsUtils.toUtcString(subscription.getActivatedAt()))
                .validUntil(SubsUtils.toUtcString(subscription.getExpiresAt()))
                .features(pack.features())
                .build();
        return NotificationUtils.generateSubscriptionSuccessEmailBody(params);
    }

}
