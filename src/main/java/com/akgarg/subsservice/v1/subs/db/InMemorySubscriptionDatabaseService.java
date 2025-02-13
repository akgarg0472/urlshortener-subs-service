package com.akgarg.subsservice.v1.subs.db;

import com.akgarg.subsservice.v1.subs.Subscription;
import com.akgarg.subsservice.v1.subs.SubscriptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
@Service
@Profile({"dev", "DEV"})
public class InMemorySubscriptionDatabaseService implements SubscriptionDatabaseService {

    private final Collection<Subscription> subscriptions = new ArrayList<>();

    @Override
    public Optional<Subscription> findActiveSubscription(final String requestId, final String userId) {
        log.info("[{}] finding active subscription for user {}", requestId, userId);
        return subscriptions
                .stream()
                .filter(subscription -> subscription.getUserId().equals(userId) &&
                        SubscriptionStatus.ACTIVE.name().equalsIgnoreCase(subscription.getStatus()))
                .findFirst();
    }

    @Override
    public Subscription addSubscription(final String requestId, final Subscription subscription) {
        log.info("[{}] adding new subscription {}", requestId, subscription);
        subscriptions.add(subscription);
        return subscription;
    }

    @Override
    public Collection<Subscription> findAllSubscriptionsForUserId(final String requestId, final String userId) {
        return subscriptions
                .stream()
                .filter(subscription -> subscription.getUserId().equalsIgnoreCase(userId))
                .sorted(Comparator.comparing(Subscription::getSubscribedAt).reversed())
                .toList();
    }

    @Override
    public Subscription updateSubscription(final String requestId, final Subscription subscription) {
        log.info("[{}] updating subscription {}", requestId, subscription);
        subscriptions.remove(subscription);
        subscriptions.add(subscription);
        return subscription;
    }

}
