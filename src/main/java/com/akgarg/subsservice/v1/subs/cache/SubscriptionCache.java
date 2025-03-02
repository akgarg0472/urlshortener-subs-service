package com.akgarg.subsservice.v1.subs.cache;

import com.akgarg.subsservice.v1.subs.SubscriptionDTO;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubscriptionCache {

    void addSubscription(SubscriptionDTO subscription);

    Optional<SubscriptionDTO> getSubscription(String userId);

    void addAllSubscriptions(String userId, List<SubscriptionDTO> subscriptions);

    Collection<SubscriptionDTO> getAllSubscriptions(String userId);

}
