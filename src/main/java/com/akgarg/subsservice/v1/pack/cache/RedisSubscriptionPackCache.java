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
@Profile({"prod", "PROD"})
@Component
@RequiredArgsConstructor
public class RedisSubscriptionPackCache implements SubscriptionPackCache {

    private static final String REDIS_SUBS_PACK_KEY = "subscription_pack";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void addOrUpdatePack(final String requestId, final SubscriptionPack subscriptionPack) {
        log.info("[{}] Adding subscription pack {}", requestId, subscriptionPack);

        try {
            redisTemplate.opsForHash().put(REDIS_SUBS_PACK_KEY, subscriptionPack.getId(), objectMapper.writeValueAsString(subscriptionPack));
            log.info("[{}] Successfully added subscription pack {}", requestId, subscriptionPack);
        } catch (Exception e) {
            log.error("Failed to add subscription pack {}", subscriptionPack, e);
        }
    }

    @Override
    public List<SubscriptionPack> getAllPacks(final String requestId, final int skip, final int limit, final boolean visible, final boolean deleted) {
        log.debug("[{}] Getting all subscription packs. skip={}, limit={}, visible={}, deleted={}", requestId, skip, limit, visible, deleted);
        final var packs = new ArrayList<SubscriptionPack>();

        try {
            final var entries = redisTemplate.opsForHash().entries(REDIS_SUBS_PACK_KEY);
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
            final var object = redisTemplate.opsForHash().get(REDIS_SUBS_PACK_KEY, packId);
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
            redisTemplate.opsForHash().delete(REDIS_SUBS_PACK_KEY, packId);
        } catch (Exception e) {
            log.error("[{}] Failed to delete subscription pack {}", requestId, packId, e);
        }
    }

}
