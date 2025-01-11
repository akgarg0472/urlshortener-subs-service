package com.akgarg.subsservice.v1.subs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("pack_id")
    private String packId;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("status")
    private SubscriptionStatus status;

    @JsonProperty("subscribed_at")
    private long subscribedAt;

    @JsonProperty("expires_at")
    private long expiresAt;

    public static SubscriptionDTO fromSubscription(final Subscription subscription) {
        final var subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setId(subscription.getId());
        subscriptionDTO.setUserId(subscription.getUserId());
        subscriptionDTO.setPackId(subscription.getPackId());
        subscriptionDTO.setAmount(subscription.getAmount());
        subscriptionDTO.setCurrency(subscription.getCurrency());
        subscriptionDTO.setStatus(subscription.getStatus());
        subscriptionDTO.setSubscribedAt(subscription.getSubscribedAt());
        subscriptionDTO.setExpiresAt(subscription.getExpiresAt());
        return subscriptionDTO;
    }

}
