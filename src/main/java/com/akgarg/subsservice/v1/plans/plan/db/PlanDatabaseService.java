package com.akgarg.subsservice.v1.plans.plan.db;

import com.akgarg.subsservice.v1.plans.plan.Plan;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface PlanDatabaseService {

    Optional<Plan> findByCode(String code);

    Plan saveOrUpdate(Plan plan);

    List<Plan> findByVisibleAndDeleted(boolean visible, boolean deleted, PageRequest pageRequest);

    Optional<Plan> findById(String planId);

    List<Plan> findAll();

}
