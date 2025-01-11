package com.akgarg.subsservice.v1.pack.cache;

import com.akgarg.subsservice.v1.pack.SubscriptionPack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Profile({"dev", "DEV"})
public class InMemorySubscriptionPackCache implements SubscriptionPackCache {

    private final Map<String, SubscriptionPack> cache;

    public InMemorySubscriptionPackCache() {
        cache = new ConcurrentHashMap<>();
    }

    @Override
    public void addOrUpdatePack(final String requestId, final SubscriptionPack subscriptionPack) {
        Objects.requireNonNull(subscriptionPack);
        log.info("[{}] Adding pack {}", requestId, subscriptionPack);
        cache.put(subscriptionPack.getId(), subscriptionPack);
    }

    @Override
    public List<SubscriptionPack> getAllPacks(
            final String requestId,
            final int skip,
            final int limit,
            final boolean visible,
            final boolean deleted
    ) {
        log.debug("[{}] Getting packs from cache", requestId);
        return cache
                .values()
                .stream()
                .filter(plan -> plan.isVisible() == visible && plan.isDeleted() == deleted)
                .skip(skip)
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<SubscriptionPack> getPackById(final String requestId, final String packId) {
        log.debug("[{}] Getting pack for packId: {}", requestId, packId);
        return Optional.of(cache.get(packId));
    }

    @Override
    public void deletePack(final String requestId, final String packId) {
        log.info("[{}] Deleting pack {}", requestId, packId);
        final var removedPack = cache.remove(packId);
        if (removedPack != null) {
            log.info("[{}] Pack {} deleted", requestId, removedPack);
        }
    }

}
