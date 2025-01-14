package com.akgarg.subsservice.notification;

import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.akgarg.subsservice.v1.subs.SubscriptionDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubscriptionNotificationEvent {

    private SubscriptionEventType eventType;
    private SubscriptionDTO subscription;
    private SubscriptionPackDTO subscriptionPack;

}
