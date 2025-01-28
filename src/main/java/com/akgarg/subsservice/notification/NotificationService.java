package com.akgarg.subsservice.notification;

import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.akgarg.subsservice.v1.subs.SubscriptionDTO;

public interface NotificationService {

    void sendSubscriptionSuccessEmail(String requestId, SubscriptionDTO subscriptionDTO, SubscriptionPackDTO subscriptionPackDTO);

}
