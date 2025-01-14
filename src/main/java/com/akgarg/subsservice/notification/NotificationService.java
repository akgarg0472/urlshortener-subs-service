package com.akgarg.subsservice.notification;

import com.akgarg.subsservice.v1.subs.SubscriptionDTO;

public interface NotificationService {

    void sendSubscriptionSuccess(String requestId, SubscriptionDTO subscriptionDTO);

}
