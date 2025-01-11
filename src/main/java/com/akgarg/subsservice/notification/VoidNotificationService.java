package com.akgarg.subsservice.notification;

import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.akgarg.subsservice.v1.subs.SubscriptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"dev", "DEV"})
public class VoidNotificationService implements NotificationService {

    @Override
    public void sendSubscriptionSuccess(final String requestId, final SubscriptionDTO subscriptionDTO) {
        log.info("[{}] Sending subscription success: {}", requestId, subscriptionDTO);
    }

    @Override
    public void sendSubscriptionPackCreated(final String requestId, final SubscriptionPackDTO subscriptionPackDTO) {
        log.info("[{}] Sending subscription package created: {}", requestId, subscriptionPackDTO);
    }

    @Override
    public void sendSubscriptionPackUpdated(final String requestId, final SubscriptionPackDTO updatedPackDTO) {
        log.info("[{}] Sending subscription package updated: {}", requestId, updatedPackDTO);
    }

    @Override
    public void sendSubscriptionPackDeleted(final String requestId, final SubscriptionPackDTO subscriptionPackDTO) {
        log.info("[{}] Sending subscription package deleted: {}", requestId, subscriptionPackDTO);
    }

}
