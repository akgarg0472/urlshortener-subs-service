package com.akgarg.subsservice.v1.plans.plan.db;

import com.akgarg.subsservice.v1.plans.plan.Plan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Profile("dev")
@Service
public class InMemoryPlanDatabaseService implements PlanDatabaseService {

    @Override
    public Optional<Plan> findByCode(final String code) {
        return Optional.empty();
    }

    @Override
    public Plan saveOrUpdate(final Plan plan) {
        return null;
    }

    @Override
    public List<Plan> findByVisibleAndDeleted(
            final boolean visible,
            final boolean deleted,
            final PageRequest pageRequest
    ) {
        return Collections.emptyList();
    }

    @Override
    public Optional<Plan> findById(final String planId) {
        return Optional.empty();
    }

    @Override
    public List<Plan> findAll() {
        return Collections.emptyList();
    }

}
