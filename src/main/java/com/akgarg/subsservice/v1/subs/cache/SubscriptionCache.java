package com.akgarg.subsservice.v1.subs.cache;

import com.akgarg.subsservice.v1.subs.SubscriptionDTO;

import java.util.Collection;
import java.util.Optional;

public interface SubscriptionCache {

    void addSubscription(SubscriptionDTO subscriptionDTO);

    Optional<SubscriptionDTO> getActiveSubscriptionByUserId(String userId);

    void addUserSubscriptions(final String userId, Collection<SubscriptionDTO> subscriptions);

    Optional<Collection<SubscriptionDTO>> getAllSubscriptionsByUserId(String userId);

}
