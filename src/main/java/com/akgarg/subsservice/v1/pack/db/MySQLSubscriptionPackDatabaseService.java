package com.akgarg.subsservice.v1.pack.db;

import com.akgarg.subsservice.v1.pack.SubscriptionPack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class MySQLSubscriptionPackDatabaseService implements SubscriptionPackDatabaseService {

    private final SubscriptionPackRepository subscriptionPackRepository;

    @Override
    public Optional<SubscriptionPack> findByPackId(final String packId) {
        log.info("Getting subscription pack by id {}", packId);

        try {
            return subscriptionPackRepository.findById(packId);
        } catch (Exception e) {
            log.error("error finding Pack by Pack Id: {}", packId, e);
        }

        return Optional.empty();
    }

    @Override
    public SubscriptionPack saveOrUpdatePack(final SubscriptionPack plan) {
        log.info("Save or Update Pack: {}", plan);
        return subscriptionPackRepository.save(plan);
    }

    @Override
    public List<SubscriptionPack> findAllByVisibleAndDeleted(final boolean visible, final boolean deleted, final PageRequest pageRequest) {
        return subscriptionPackRepository.findAllByVisibleAndDeleted(visible, deleted, pageRequest);
    }

    @Override
    public Optional<SubscriptionPack> findDefaultSubscriptionPack() {
        log.info("Find Default Subscription Pack");
        return subscriptionPackRepository.findByDefaultPackTrue();
    }

}
