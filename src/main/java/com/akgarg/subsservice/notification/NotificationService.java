package com.akgarg.subsservice.notification;

import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.akgarg.subsservice.v1.subs.SubscriptionDTO;

public interface NotificationService {

    void sendSubscriptionSuccess(String requestId, SubscriptionDTO subscriptionDTO);

    void sendSubscriptionPackCreated(String requestId, SubscriptionPackDTO subscriptionPackDTO);

    void sendSubscriptionPackUpdated(String requestId, SubscriptionPackDTO updatedPackDTO);

    void sendSubscriptionPackDeleted(String requestId, SubscriptionPackDTO subscriptionPackDTO);

}
