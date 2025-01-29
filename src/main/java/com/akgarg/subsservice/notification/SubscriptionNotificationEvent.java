package com.akgarg.subsservice.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubscriptionNotificationEvent {

    private NotificationType notificationType;
    private String[] recipients;
    @JsonProperty("isHtml")
    private boolean isHtml;
    private String subject;
    private String body;

}
