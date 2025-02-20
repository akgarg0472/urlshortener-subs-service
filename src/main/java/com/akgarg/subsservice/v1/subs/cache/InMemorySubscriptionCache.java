package com.akgarg.subsservice.v1.subs.cache;

import com.akgarg.subsservice.v1.subs.SubscriptionDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Profile({"dev", "DEV"})
@Component
public class InMemorySubscriptionCache implements SubscriptionCache {

    /**
     * The interval at which the eviction task runs to remove expired entries, in milliseconds.
     */
    private static final long EVICTION_INTERVAL_MS = 30 * 1000L;

    /**
     * Executor for running the eviction task at fixed intervals.
     */
    private final ScheduledExecutorService evictionExecutor = Executors.newSingleThreadScheduledExecutor();

    private final Map<String, SubscriptionDTO> subscriptions = new HashMap<>();
    private final Map<String, Collection<SubscriptionDTO>> groupedSubscriptions = new HashMap<>();

    /**
     * Starts the eviction task after the bean is initialized.
     * The eviction task will run periodically to remove expired entries from the maps.
     */
    @PostConstruct
    public void startEvictionTask() {
        evictionExecutor.scheduleAtFixedRate(this::evictExpiredEntries,
                EVICTION_INTERVAL_MS, EVICTION_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void addSubscription(final SubscriptionDTO subscriptionDTO) {
        log.info("Adding subscription {} to cache", Objects.requireNonNull(subscriptionDTO));
        subscriptions.put(subscriptionDTO.getId(), subscriptionDTO);
    }

    @Override
    public Optional<SubscriptionDTO> getActiveSubscriptionByUserId(final String userId) {
        log.info("Getting subscription for user {} from cache", Objects.requireNonNull(userId));
        return Optional.ofNullable(subscriptions.get(userId));
    }

    @Override
    public void addUserSubscriptions(final String userId, final Collection<SubscriptionDTO> subscriptions) {
        log.info("Adding all subscription to cache for userId {}", Objects.requireNonNull(userId));
        groupedSubscriptions.put(userId, subscriptions);
    }

    @Override
    public Optional<Collection<SubscriptionDTO>> getAllSubscriptionsByUserId(final String userId) {
        log.debug("Getting all subscriptions from cache for userId {}", Objects.requireNonNull(userId));
        return Optional.of(groupedSubscriptions.getOrDefault(userId, Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(SubscriptionDTO::getActivatedAt).reversed())
                .toList());
    }

    /**
     * Periodically evicts expired entries from the maps.
     * Entries are considered expired if they are older than the TTL period.
     */
    private void evictExpiredEntries() {
        final var currentTime = System.currentTimeMillis();
        final var iterator = subscriptions.entrySet().iterator();

        while (iterator.hasNext()) {
            final var entry = iterator.next();
            final var key = entry.getKey();
            final var expirationTime = entry.getValue().getExpiresAt();

            if (currentTime >= expirationTime) {
                subscriptions.remove(key);
                iterator.remove();
            }
        }
    }

    /**
     * Shuts down the eviction task when the service is destroyed.
     */
    @PreDestroy
    public void stopEvictionTask() {
        evictionExecutor.shutdownNow();
    }

}
