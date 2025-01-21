package com.akgarg.subsservice.v1.pack.cache;

import com.akgarg.subsservice.v1.pack.SubscriptionPack;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class RedisSubscriptionPackCache implements SubscriptionPackCache {

    private static final String REDIS_SUBS_PACK_HASH_KEY = "subs:pack";
    private static final String REDIS_DEFAULT_SUBS_PACK_ID = "default:subs:pack";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void addOrUpdatePack(final String requestId, final SubscriptionPack subscriptionPack) {
        log.info("[{}] Adding subscription pack {}", requestId, subscriptionPack);

        try {
            final var pack = objectMapper.writeValueAsString(subscriptionPack);
            redisTemplate.opsForHash().put(REDIS_SUBS_PACK_HASH_KEY, subscriptionPack.getId(), pack);

            if (Boolean.TRUE.equals(subscriptionPack.getDefaultPack())) {
                redisTemplate.opsForValue().set(REDIS_DEFAULT_SUBS_PACK_ID, pack);
            }

            log.info("[{}] Successfully added subscription pack", requestId);
        } catch (Exception e) {
            log.error("[{}] Failed to add subscription pack", requestId, e);
        }
    }

    @Override
    public List<SubscriptionPack> getAllPacks(final String requestId, final int skip, final int limit, final boolean visible, final boolean deleted) {
        log.debug("[{}] Getting all subscription packs. skip={}, limit={}, visible={}, deleted={}", requestId, skip, limit, visible, deleted);
        final var packs = new ArrayList<SubscriptionPack>();

        try {
            final var entries = redisTemplate.opsForHash().entries(REDIS_SUBS_PACK_HASH_KEY);
            for (final var pack : entries.values()) {
                packs.add(objectMapper.readValue(pack.toString(), SubscriptionPack.class));
            }
        } catch (Exception e) {
            log.error("[{}] Failed to retrieve all subscription packs", requestId, e);
        }

        return packs;
    }

    @Override
    public Optional<SubscriptionPack> getPackById(final String requestId, final String packId) {
        try {
            log.info("[{}] Getting subscription pack by id {}", requestId, packId);
            final var object = redisTemplate.opsForHash().get(REDIS_SUBS_PACK_HASH_KEY, packId);
            if (object != null) {
                return Optional.of(objectMapper.readValue(object.toString(), SubscriptionPack.class));
            }
        } catch (Exception e) {
            log.error("[{}] Failed to fetch subscription pack {}", requestId, packId, e);
        }
        return Optional.empty();
    }

    @Override
    public void deletePack(final String requestId, final String packId) {
        log.info("[{}] Deleting subscription pack {}", requestId, packId);

        try {
            redisTemplate.opsForHash().delete(REDIS_SUBS_PACK_HASH_KEY, packId);
        } catch (Exception e) {
            log.error("[{}] Failed to delete subscription pack {}", requestId, packId, e);
        }
    }

    @Override
    public Optional<SubscriptionPack> getDefaultSubscriptionPack(final String requestId) {
        return getPackById(requestId, REDIS_DEFAULT_SUBS_PACK_ID);
    }

}
