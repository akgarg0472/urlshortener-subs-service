package com.akgarg.subsservice.v1.plans.plan.cache;

import com.akgarg.subsservice.v1.plans.plan.Plan;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class RedisPlanCache implements PlanCache {

    private static final Logger LOGGER = LogManager.getLogger(RedisPlanCache.class);
    private static final String REDIS_HASH_FIELD = "subs_plan";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void addOrUpdatePlan(final Plan plan) {
        try {
            final String planCode = plan.getCode();
            redisTemplate
                    .opsForHash()
                    .put(REDIS_HASH_FIELD, planCode, plan);
        } catch (Exception e) {
            LOGGER.error("Error adding/updating subscription plan in Redis", e);
        }
    }

    @Override
    public List<Plan> getAllPlans(final int skip, final int limit, final boolean visible, final boolean deleted) {
        try {
            return redisTemplate
                    .opsForHash()
                    .values(REDIS_HASH_FIELD)
                    .stream()
                    .map(Plan.class::cast)
                    .filter(plan -> plan.isVisible() == visible && plan.isDeleted() == deleted)
                    .skip(skip)
                    .limit(limit)
                    .toList();
        } catch (Exception e) {
            LOGGER.error("Failed to fetch subscription plans from Redis", e);
            return Collections.emptyList();
        }
    }

}
