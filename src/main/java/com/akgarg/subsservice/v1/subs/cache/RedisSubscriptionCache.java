package com.akgarg.subsservice.v1.subs.cache;

import com.akgarg.subsservice.v1.subs.SubscriptionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Profile({"prod", "PROD"})
@Component
@RequiredArgsConstructor
public class RedisSubscriptionCache implements SubscriptionCache {

    private static final String SUBSCRIPTION_KEY_PREFIX = "subscription:";
    private static final String ALL_SUBSCRIPTION_MAP_KEY = "subscription:all:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void addSubscription(final SubscriptionDTO subscriptionDTO) {
        Objects.requireNonNull(subscriptionDTO);
        log.info("Adding subscription to cache with id: {}", subscriptionDTO.getId());

        try {
            final var key = createSubscriptionKey(subscriptionDTO.getUserId());
            final var timeout = subscriptionDTO.getExpiresAt() - System.currentTimeMillis();
            final var subscription = objectMapper.writeValueAsString(subscriptionDTO);
            redisTemplate.opsForValue().set(key, subscription, timeout, TimeUnit.MILLISECONDS);

            if (log.isDebugEnabled()) {
                log.debug("Successfully added subscription to cache");
            }
        } catch (Exception e) {
            log.error("Failed to add subscription to cache: {}", subscriptionDTO, e);
        }
    }

    @Override
    public Optional<SubscriptionDTO> getSubscription(final String userId) {
        log.info("Getting subscription from cache for userId {}", userId);

        try {
            final var object = redisTemplate.opsForValue().get(createSubscriptionKey(userId));
            if (Objects.nonNull(object)) {
                log.info("Successfully get subscription from cache for userId {}", userId);
                return Optional.ofNullable(objectMapper.readValue(object, SubscriptionDTO.class));
            }
        } catch (Exception e) {
            log.error("Failed to retrieve subscription from cache for userId {}", userId, e);
        }

        return Optional.empty();
    }

    @Override
    public void addAllSubscriptions(final String userId, final List<SubscriptionDTO> subscriptions) {
        log.debug("Adding subscriptions to cache for userId {}", userId);

        try {
            final var key = createAllSubscriptionsKey(userId);
            final var subscription = objectMapper.writeValueAsString(subscriptions);
            redisTemplate.opsForValue().set(key, subscription, 5 * 60 * 1000L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Failed to add subscriptions to cache for userId {}", userId, e);
        }
    }

    @Override
    public Collection<SubscriptionDTO> getAllSubscriptions(final String userId) {
        log.info("Getting subscriptions from cache for userId {}", userId);

        try {
            final var object = redisTemplate.opsForValue().get(createAllSubscriptionsKey(userId));
            if (Objects.nonNull(object)) {
                return objectMapper.readValue(object, new TypeReference<List<SubscriptionDTO>>() {
                });
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to retrieve subscriptions from cache for userId {}", userId, e);
            return Collections.emptyList();
        }
    }

    private String createSubscriptionKey(final String userId) {
        return SUBSCRIPTION_KEY_PREFIX + userId;
    }

    private String createAllSubscriptionsKey(final String userId) {
        return ALL_SUBSCRIPTION_MAP_KEY + userId;
    }

}
