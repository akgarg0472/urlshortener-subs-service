package com.akgarg.subsservice.subs.v1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    Optional<Subscription> findFirstByUserIdOrderByExpiresAtDesc(final String userId);

}
