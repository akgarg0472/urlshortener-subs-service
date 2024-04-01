package com.akgarg.subsservice.plans.v1.plan.cache;

import com.akgarg.subsservice.plans.v1.plan.Plan;

import java.util.List;

public interface PlanCache {

    void addOrUpdatePlan(Plan plan);

    List<Plan> getAllPlans(int skip, int limit, boolean visible, boolean deleted);

}
