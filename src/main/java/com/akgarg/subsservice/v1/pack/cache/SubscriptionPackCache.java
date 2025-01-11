package com.akgarg.subsservice.v1.pack.cache;

import com.akgarg.subsservice.v1.pack.SubscriptionPack;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPackCache {

    void addOrUpdatePack(String requestId, SubscriptionPack subscriptionPack);

    List<SubscriptionPack> getAllPacks(String requestId, int skip, int limit, boolean visible, boolean deleted);

    Optional<SubscriptionPack> getPackById(final String requestId, String packId);

    void deletePack(String requestId, String packId);

}
