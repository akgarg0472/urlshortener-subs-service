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
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("dev")
public class InMemorySubscriptionPackCache implements SubscriptionPackCache {

    private final Map<String, SubscriptionPack> subscriptionPacks;

    public InMemorySubscriptionPackCache() {
        subscriptionPacks = new ConcurrentHashMap<>();
    }

    @Override
    public void addOrUpdatePack(final SubscriptionPack subscriptionPack) {
        Objects.requireNonNull(subscriptionPack);
        log.info("Adding Subscription pack {}", subscriptionPack);
        subscriptionPacks.put(subscriptionPack.getId(), subscriptionPack);
    }

    @Override
    public List<SubscriptionPack> getAllPacks(
            final int skip,
            final int limit,
            final boolean visible,
            final boolean deleted
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving subscription packs from cache");
        }

        return subscriptionPacks
                .values()
                .stream()
                .filter(plan -> plan.isVisible() == visible && plan.isDeleted() == deleted)
                .skip(skip)
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<SubscriptionPack> getPackById(final String packId) {
        log.info("Getting subscription pack by id {}", packId);
        return Optional.of(subscriptionPacks.get(packId));
    }

    @Override
    public void deletePack(final String packId) {
        log.warn("Deleting subscription pack with id: {}", packId);
        final var removedPack = subscriptionPacks.remove(packId);
        if (removedPack != null) {
            log.warn("Subscription pack having id {} deleted", removedPack);
        }
    }

    @Override
    public Optional<SubscriptionPack> getDefaultSubscriptionPack() {
        return subscriptionPacks.values()
                .stream()
                .filter(SubscriptionPack::getDefaultPack)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        filteredList -> {
                            if (filteredList.size() > 1) {
                                throw new IllegalStateException("Too many default subscription packs configured");
                            }
                            return filteredList.stream().findFirst();
                        }
                ));
    }

}
