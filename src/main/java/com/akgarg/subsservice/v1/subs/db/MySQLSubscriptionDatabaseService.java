package com.akgarg.subsservice.v1.subs.db;

import com.akgarg.subsservice.v1.subs.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Profile("prod")
@Service
@RequiredArgsConstructor
public class MySQLSubscriptionDatabaseService implements SubscriptionDatabaseService {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public Optional<Subscription> findFirstByUserIdOrderByExpiresAtDesc(final String userId) {
        return subscriptionRepository.findFirstByUserIdOrderByExpiresAtDesc(userId);
    }

    @Override
    public Subscription saveOrUpdate(final Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

}
