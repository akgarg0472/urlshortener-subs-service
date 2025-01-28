package com.akgarg.subsservice.notification;

import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.akgarg.subsservice.v1.subs.SubscriptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"dev", "default"})
public class VoidNotificationService implements NotificationService {

    @Override
    public void sendSubscriptionSuccessEmail(final String requestId, final SubscriptionDTO subscriptionDTO, final SubscriptionPackDTO subscriptionPackDTO) {
        log.info("[{}] Sending subscription success: {}", requestId, subscriptionDTO);
    }

}
