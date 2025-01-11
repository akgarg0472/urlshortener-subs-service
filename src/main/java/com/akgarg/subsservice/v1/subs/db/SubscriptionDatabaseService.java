package com.akgarg.subsservice.v1.subs.db;

import com.akgarg.subsservice.v1.subs.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionDatabaseService {

    Optional<Subscription> findActiveSubscription(String requestId, String userId);

    Subscription addSubscription(String requestId, Subscription subscription);

    List<Subscription> findAllActiveSubscriptions();

}
