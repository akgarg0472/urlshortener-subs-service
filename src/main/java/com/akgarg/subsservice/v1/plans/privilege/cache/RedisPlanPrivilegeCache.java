package com.akgarg.subsservice.v1.plans.privilege.cache;

import com.akgarg.subsservice.v1.plans.privilege.PlanPrivilege;
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
public class RedisPlanPrivilegeCache implements PlanPrivilegeCache {

    private static final Logger LOGGER = LogManager.getLogger(RedisPlanPrivilegeCache.class);
    private static final String REDIS_HASH_FIELD = "subs_plan_privilege";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void addOrUpdatePlanPrivilege(final PlanPrivilege planPrivilege) {
        try {
            final int privilegeId = planPrivilege.getId();
            redisTemplate
                    .opsForHash()
                    .put(REDIS_HASH_FIELD, privilegeId, planPrivilege);
        } catch (Exception e) {
            LOGGER.error("Error adding/updating subscription plan privilege in Redis", e);
        }
    }

    @Override
    public List<PlanPrivilege> getAllPlanPrivileges() {
        try {
            return redisTemplate
                    .opsForHash()
                    .values(REDIS_HASH_FIELD)
                    .stream()
                    .map(PlanPrivilege.class::cast)
                    .toList();
        } catch (Exception e) {
            LOGGER.error("Failed to fetch subscription plan privileges from Redis", e);
            return Collections.emptyList();
        }
    }

}
