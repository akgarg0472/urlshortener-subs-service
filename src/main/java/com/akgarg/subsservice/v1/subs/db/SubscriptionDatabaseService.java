package com.akgarg.subsservice.v1.subs.db;

import com.akgarg.subsservice.v1.subs.Subscription;

import java.util.Collection;
import java.util.Optional;

public interface SubscriptionDatabaseService {

    Optional<Subscription> findActiveSubscription(String requestId, String userId);

    Subscription addSubscription(String requestId, Subscription subscription);

    Collection<Subscription> findAllSubscriptionsForUserId(String requestId, String userId);

    void updateSubscription(String requestId, Subscription subscription);

}
