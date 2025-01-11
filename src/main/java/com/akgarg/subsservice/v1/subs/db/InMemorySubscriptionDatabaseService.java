package com.akgarg.subsservice.v1.subs.db;

import com.akgarg.subsservice.v1.subs.Subscription;
import com.akgarg.subsservice.v1.subs.SubscriptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Profile({"dev", "DEV"})
public class InMemorySubscriptionDatabaseService implements SubscriptionDatabaseService {

    private final Map<String, Subscription> subscriptions = new HashMap<>();

    @Override
    public Optional<Subscription> findActiveSubscription(final String requestId, final String userId) {
        log.info("[{}] finding active subscription for user {}", requestId, userId);
        return subscriptions.values()
                .stream()
                .filter(subscription -> subscription.getUserId().equals(userId) && subscription.getStatus() == SubscriptionStatus.ACTIVE)
                .findFirst();
    }

    @Override
    public Subscription addSubscription(final String requestId, final Subscription subscription) {
        log.info("[{}] adding new subscription {}", requestId, subscription);
        subscriptions.put(subscription.getUserId(), subscription);
        return subscription;
    }

    @Override
    public List<Subscription> findAllActiveSubscriptions() {
        return subscriptions.values()
                .stream()
                .filter(subscription -> subscription.getStatus() == SubscriptionStatus.ACTIVE)
                .toList();
    }

}
