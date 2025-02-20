package com.akgarg.subsservice.v1.pack.cache;

import com.akgarg.subsservice.v1.pack.SubscriptionPack;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class RedisSubscriptionPackCache implements SubscriptionPackCache {

    private static final long SUBSCRIPTION_CACHE_EXPIRATION = TimeUnit.MINUTES.toMillis(1);
    private static final String REDIS_SUBS_PACK_KEY = "subs:pack";
    private static final String REDIS_DEFAULT_SUBS_PACK_ID = "default:subs:pack";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void addOrUpdatePack(final SubscriptionPack subscriptionPack) {
        log.info("Adding subscription pack {}", subscriptionPack);

        try {
            final var pack = objectMapper.writeValueAsString(subscriptionPack);
            redisTemplate.opsForValue().set(createPackKey(subscriptionPack.getId()), pack, SUBSCRIPTION_CACHE_EXPIRATION, TimeUnit.MILLISECONDS);

            if (Boolean.TRUE.equals(subscriptionPack.getDefaultPack())) {
                redisTemplate.opsForValue().set(REDIS_DEFAULT_SUBS_PACK_ID, pack, SUBSCRIPTION_CACHE_EXPIRATION, TimeUnit.MILLISECONDS);
            }

            log.info("Successfully added subscription pack");
        } catch (Exception e) {
            log.error("Failed to add subscription pack", e);
        }
    }

    @Override
    public List<SubscriptionPack> getAllPacks(final int skip, final int limit, final boolean visible, final boolean deleted) {
        log.debug("Getting all subscription packs. skip={}, limit={}, visible={}, deleted={}", skip, limit, visible, deleted);
        final var packs = new ArrayList<SubscriptionPack>();

        try {
            final var keys = getSubscriptionPackKeys();
            if (!keys.isEmpty()) {
                for (final var pack : Objects.requireNonNull(redisTemplate.opsForValue().multiGet(keys))) {
                    packs.add(objectMapper.readValue(pack, SubscriptionPack.class));
                }
            }
        } catch (Exception e) {
            log.error("Failed to retrieve all subscription packs", e);
        }

        return packs;
    }

    @Override
    public Optional<SubscriptionPack> getPackById(final String packId) {
        try {
            log.info("Getting subscription pack by id {}", packId);
            final var object = redisTemplate.opsForValue().get(createPackKey(packId));
            if (object != null) {
                return Optional.of(objectMapper.readValue(object, SubscriptionPack.class));
            }
        } catch (Exception e) {
            log.error("Failed to fetch subscription pack {}", packId, e);
        }
        return Optional.empty();
    }

    @Override
    public void deletePack(final String packId) {
        log.info("Deleting subscription pack {}", packId);

        try {
            redisTemplate.delete(createPackKey(packId));
        } catch (Exception e) {
            log.error("Failed to delete subscription pack {}", packId, e);
        }
    }

    @Override
    public Optional<SubscriptionPack> getDefaultSubscriptionPack() {
        return getPackById(REDIS_DEFAULT_SUBS_PACK_ID);
    }

    private String createPackKey(final String packId) {
        return REDIS_SUBS_PACK_KEY + ":" + packId;
    }

    private ArrayList<String> getSubscriptionPackKeys() {
        final var scanOptions = ScanOptions.scanOptions()
                .match(REDIS_SUBS_PACK_KEY + "*")
                .build();

        final var connectionFactory = redisTemplate.getConnectionFactory();

        if (Objects.isNull(connectionFactory)) {
            throw new IllegalStateException("Redis connection factory not set");
        }

        final var keys = new ArrayList<String>();

        try (final var cursor = connectionFactory.getConnection().keyCommands().scan(scanOptions)) {
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
        }

        return keys;
    }

}
