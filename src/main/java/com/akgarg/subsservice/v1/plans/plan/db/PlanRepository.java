package com.akgarg.subsservice.v1.plans.plan.db;

import com.akgarg.subsservice.v1.plans.plan.Plan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface PlanRepository extends JpaRepository<Plan, String> {

    List<Plan> findByVisibleAndDeleted(
            final boolean visible,
            final boolean expired,
            final Pageable pageable
    );

    Optional<Plan> findByCode(final String code);

}
