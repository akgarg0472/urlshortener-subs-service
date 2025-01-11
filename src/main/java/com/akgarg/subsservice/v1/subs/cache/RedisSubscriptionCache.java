package com.akgarg.subsservice.v1.subs.cache;

import com.akgarg.subsservice.v1.subs.SubscriptionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Profile({"prod", "PROD"})
@Component
@RequiredArgsConstructor
public class RedisSubscriptionCache implements SubscriptionCache {

    private static final String SUBSCRIPTION_KEY_PREFIX = "subscription:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void addSubscription(final String requestId, final SubscriptionDTO subscriptionDTO) {
        Objects.requireNonNull(subscriptionDTO);
        log.info("[{}] Adding subscription to cache: {}", requestId, subscriptionDTO);

        try {
            final var key = createKey(subscriptionDTO.getUserId());
            final var timeout = subscriptionDTO.getExpiresAt() - System.currentTimeMillis();
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(subscriptionDTO), timeout, TimeUnit.MILLISECONDS);
            log.info("[{}] Successfully added subscription to cache: {}", requestId, subscriptionDTO);
        } catch (Exception e) {
            log.error("[{}] Failed to add subscription to cache: {}", requestId, subscriptionDTO, e);
        }
    }

    @Override
    public Optional<SubscriptionDTO> getSubscriptionByUserId(final String requestId, final String userId) {
        log.info("[{}] Getting subscription from cache: {}", requestId, userId);

        try {
            final var object = redisTemplate.opsForValue().get(createKey(userId));
            if (Objects.nonNull(object)) {
                log.info("[{}] Successfully get subscription from cache: {}", requestId, userId);
                return Optional.ofNullable(objectMapper.readValue(object, SubscriptionDTO.class));
            }
        } catch (Exception e) {
            log.error("[{}] Failed to retrieve subscription from cache: {}", requestId, userId, e);
        }

        return Optional.empty();
    }

    private String createKey(final String userId) {
        return SUBSCRIPTION_KEY_PREFIX + userId;
    }

}
