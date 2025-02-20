package com.akgarg.subsservice.v1.subs.db;

import com.akgarg.subsservice.v1.subs.Subscription;

import java.util.Collection;
import java.util.Optional;

public interface SubscriptionDatabaseService {

    Optional<Subscription> findActiveSubscription(String userId);

    Subscription addSubscription(Subscription subscription);

    Collection<Subscription> findAllSubscriptionsForUserId(String userId);

    Subscription updateSubscription(Subscription subscription);

}
