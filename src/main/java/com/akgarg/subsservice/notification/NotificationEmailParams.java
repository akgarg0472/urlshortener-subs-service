package com.akgarg.subsservice.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class NotificationEmailParams {

    private String logoUrl;
    private String name;
    private String subscriptionId;
    private String subscriptionPack;
    private String activeAt;
    private String validUntil;
    private List<String> features;
    private String dashboardLink;

}
