package com.akgarg.subsservice.v1.plans.plan.cache;

import com.akgarg.subsservice.v1.plans.plan.Plan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Profile("dev")
@Component
public class InMemoryPlanCache implements PlanCache {

    private final Map<String, Plan> cache;

    public InMemoryPlanCache() {
        cache = new ConcurrentHashMap<>();
    }

    @Override
    public void addOrUpdatePlan(final Plan plan) {
        Objects.requireNonNull(plan);
        cache.put(plan.getCode(), plan);
    }

    @Override
    public List<Plan> getAllPlans(
            final int skip,
            final int limit,
            final boolean visible,
            final boolean deleted
    ) {
        return cache
                .values()
                .stream()
                .filter(plan -> plan.isVisible() == visible && plan.isDeleted() == deleted)
                .skip(skip)
                .limit(limit)
                .toList();
    }

}
