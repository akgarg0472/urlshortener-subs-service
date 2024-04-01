package com.akgarg.subsservice.v1.subs.db;

import com.akgarg.subsservice.v1.subs.Subscription;

import java.util.Optional;

public interface SubscriptionDatabaseService {

    Optional<Subscription> findFirstByUserIdOrderByExpiresAtDesc(String userId);

    Subscription saveOrUpdate(Subscription subscription);

}
