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
    public Optional<Subscription> getActiveSubscription(final String userId) {
        log.info("finding active subscription for userId {}", userId);

        try {
            final var activeSubscription = subscriptionRepository.findByUserIdAndStatusEqualsIgnoreCase(userId, SubscriptionStatus.ACTIVE.name());
            if (activeSubscription.isEmpty() || activeSubscription.get().getExpiresAt() < System.currentTimeMillis()) {
                return Optional.empty();
            }
            return activeSubscription;
        } catch (Exception e) {
            log.error("Failed to find active subscription for userId {}", userId, e);
        }
        return Optional.empty();
    }

    @Override
    public Subscription addSubscription(final Subscription subscription) {
        log.info("Adding new subscription with id {}", subscription.getId());

        try {
            return subscriptionRepository.save(subscription);
        } catch (Exception e) {
            log.error("Failed to add new subscription", e);
            throw e;
        }
    }

    @Override
    public Collection<Subscription> findAllSubscriptionsForUserId(final String userId) {
        try {
            return subscriptionRepository.findAllByUserIdOrderBySubscribedAtDesc(userId);
        } catch (Exception e) {
            log.error("Error finding all subscriptions for userId {}", userId, e);
            throw e;
        }
    }

    @Override
    public Subscription updateSubscription(final Subscription subscription) {
        log.info("Updating subscription with id {}", subscription.getId());

        try {
            return subscriptionRepository.save(subscription);
        } catch (Exception e) {
            log.error("Failed to update subscription with id{}", subscription.getId(), e);
            throw e;
        }
    }

}
