package com.akgarg.subsservice.v1.pack.db;

import com.akgarg.subsservice.v1.pack.SubscriptionPack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Profile("dev")
public class InMemorySubscriptionPackDatabaseService implements SubscriptionPackDatabaseService {

    private final Map<String, SubscriptionPack> subscriptionPacks = new HashMap<>();

    @Override
    public Optional<SubscriptionPack> findByPackId(final String packId) {
        log.info("Finding subscription pack by packId: {}", packId);
        return Optional.of(subscriptionPacks.get(packId));
    }

    @Override
    public SubscriptionPack saveOrUpdatePack(final SubscriptionPack subscriptionPack) {
        log.info("Saving/updating subscription pack: {}", subscriptionPack);
        subscriptionPacks.put(subscriptionPack.getId(), subscriptionPack);
        return subscriptionPack;
    }

    @Override
    public List<SubscriptionPack> findAllByVisibleAndDeleted(final boolean visible, final boolean deleted, final PageRequest pageRequest) {
        return subscriptionPacks.values()
                .stream()
                .filter(pack -> pack.isVisible() == visible && pack.isDeleted() == deleted)
                .skip((long) pageRequest.getPageNumber() * pageRequest.getPageSize())
                .limit(pageRequest.getPageSize())
                .toList();
    }

    @Override
    public Optional<SubscriptionPack> findDefaultSubscriptionPack() {
        return Optional.empty();
    }

}
