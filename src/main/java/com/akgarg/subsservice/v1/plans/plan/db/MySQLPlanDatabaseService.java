package com.akgarg.subsservice.v1.plans.plan.db;

import com.akgarg.subsservice.v1.plans.plan.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Profile("prod")
@Service
@RequiredArgsConstructor
public class MySQLPlanDatabaseService implements PlanDatabaseService {

    private final PlanRepository planRepository;

    @Override
    public Optional<Plan> findByCode(final String code) {
        return planRepository.findByCode(code);
    }

    @Override
    public Plan saveOrUpdate(final Plan plan) {
        return planRepository.save(plan);
    }

    @Override
    public List<Plan> findByVisibleAndDeleted(
            final boolean visible,
            final boolean deleted,
            final PageRequest pageRequest
    ) {
        return planRepository.findByVisibleAndDeleted(visible, deleted, pageRequest);
    }

    @Override
    public Optional<Plan> findById(final String planId) {
        return planRepository.findById(planId);
    }

    @Override
    public List<Plan> findAll() {
        return planRepository.findAll();
    }

}
