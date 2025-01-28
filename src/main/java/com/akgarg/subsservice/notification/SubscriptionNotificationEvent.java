package com.akgarg.subsservice.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubscriptionNotificationEvent {

    private NotificationType notificationType;
    private String[] recipient;
    private boolean isHtml;
    private String subject;
    private String body;

}
