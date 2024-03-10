package com.akgarg.subsservice.plans.v1;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, String> {

    List<Plan> findByVisibleAndDeleted(
            final boolean visible,
            final boolean expired,
            final Pageable pageable
    );

}
