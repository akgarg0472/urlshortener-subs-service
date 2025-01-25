package com.akgarg.subsservice.v1.subs.db;

import com.akgarg.subsservice.v1.subs.Subscription;
import com.akgarg.subsservice.v1.subs.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class MySQLSubscriptionDatabaseService implements SubscriptionDatabaseService {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public Optional<Subscription> findActiveSubscription(final String requestId, final String userId) {
        log.info("[{}] finding active subscription for user {}", requestId, userId);

        try {
            return subscriptionRepository.findByUserIdAndStatusEqualsIgnoreCase(userId, SubscriptionStatus.ACTIVE.name());
        } catch (Exception e) {
            log.error("[{}] failed to find active subscription for user {}", requestId, userId, e);
        }
        return Optional.empty();
    }

    @Override
    public Subscription addSubscription(final String requestId, final Subscription subscription) {
        log.info("[{}] adding new subscription with id: {}", requestId, subscription.getId());

        try {
            return subscriptionRepository.save(subscription);
        } catch (Exception e) {
            log.error("[{}] failed to add new subscription", requestId, e);
            throw e;
        }
    }

    @Override
    public Collection<Subscription> findAllSubscriptionsForUserId(final String requestId, final String userId) {
        try {
            return subscriptionRepository.findAllByUserIdOrderBySubscribedAtDesc(userId);
        } catch (Exception e) {
            log.error("Error finding all subscriptions for user {}", userId, e);
            throw e;
        }
    }

    @Override
    public void updateSubscription(final String requestId, final Subscription subscription) {
        log.info("[{}] updating subscription with id: {}", requestId, subscription.getId());

        try {
            subscriptionRepository.save(subscription);
        } catch (Exception e) {
            log.error("[{}] failed to update subscription {}", requestId, subscription, e);
            throw e;
        }
    }

}
