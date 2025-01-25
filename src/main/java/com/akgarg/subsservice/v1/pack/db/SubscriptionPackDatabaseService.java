package com.akgarg.subsservice.v1.pack.db;

import com.akgarg.subsservice.v1.pack.SubscriptionPack;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPackDatabaseService {

    Optional<SubscriptionPack> findByPackId(String requestId, String packId);

    SubscriptionPack saveOrUpdatePack(String requestId, SubscriptionPack plan);

    List<SubscriptionPack> findAllByVisibleAndDeleted(boolean visible, boolean deleted, PageRequest pageRequest);

    Optional<SubscriptionPack> findDefaultSubscriptionPack(String requestId);

}
