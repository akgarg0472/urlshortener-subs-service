package com.akgarg.subsservice.subs.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionDTO {

    @JsonProperty("subscription_id")
    private String id;

    @JsonProperty("plan_id")
    private String planId;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("privileges")
    private String[] privileges;

    @JsonProperty("subscribed_at")
    private long subscribedAt;

    @JsonProperty("expiring_at")
    private long expiresAt;

}
