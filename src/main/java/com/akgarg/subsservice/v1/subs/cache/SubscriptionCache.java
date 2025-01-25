package com.akgarg.subsservice.v1.subs.cache;

import com.akgarg.subsservice.v1.subs.SubscriptionDTO;

import java.util.Collection;
import java.util.Optional;

public interface SubscriptionCache {

    void addSubscription(String requestId, SubscriptionDTO subscriptionDTO);

    Optional<SubscriptionDTO> getActiveSubscriptionByUserId(String requestId, String userId);

    void addUserSubscriptions(String requestId, final String userId, Collection<SubscriptionDTO> subscriptions);

    Optional<Collection<SubscriptionDTO>> getAllSubscriptionsByUserId(String requestId, String userId);

}
