package com.akgarg.subsservice.v1.subs.db;

import com.akgarg.subsservice.v1.subs.Subscription;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Profile("dev")
@Service
public class InMemorySubscriptionDatabaseService implements SubscriptionDatabaseService {

    @Override
    public Optional<Subscription> findFirstByUserIdOrderByExpiresAtDesc(final String userId) {
        return Optional.empty();
    }

    @Override
    public Subscription saveOrUpdate(final Subscription subscription) {
        return null;
    }
    
}
