package com.akgarg.subsservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSubscriptionResponseSubscription {

    @JsonProperty("subscription_id")
    private String subscriptionId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("pack_id")
    private String packId;

    @JsonProperty("activated_at")
    private long activatedAt;

    @JsonProperty("expires_at")
    private long expiresAt;

    @JsonProperty("status")
    private String status;

}
