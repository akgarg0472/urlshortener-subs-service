package com.akgarg.subsservice.v1.pack.cache;

import com.akgarg.subsservice.v1.pack.SubscriptionPack;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPackCache {

    void addOrUpdatePack(SubscriptionPack subscriptionPack);

    List<SubscriptionPack> getAllPacks(int skip, int limit, boolean visible, boolean deleted);

    Optional<SubscriptionPack> getPackById(String packId);

    void deletePack(String packId);

    Optional<SubscriptionPack> getDefaultSubscriptionPack();

}
