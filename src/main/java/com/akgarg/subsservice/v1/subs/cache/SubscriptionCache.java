package com.akgarg.subsservice.v1.subs.cache;

import com.akgarg.subsservice.v1.subs.SubscriptionDTO;

import java.util.Optional;

public interface SubscriptionCache {

    void addSubscription(String requestId, SubscriptionDTO subscriptionDTO);

    Optional<SubscriptionDTO> getSubscriptionByUserId(String requestId, String userId);

}
