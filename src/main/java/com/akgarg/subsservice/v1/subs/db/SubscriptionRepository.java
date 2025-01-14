package com.akgarg.subsservice.v1.subs.db;

import com.akgarg.subsservice.v1.subs.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    Optional<Subscription> findByUserIdAndStatusEqualsIgnoreCase(String userId, String status);

    List<Subscription> findAllByStatus(String status);

    List<Subscription> findAllByUserIdOrderBySubscribedAtDesc(String userId);

}
