package com.akgarg.subsservice.v1.plans.plan.cache;

import com.akgarg.subsservice.v1.plans.plan.Plan;

import java.util.List;

public interface PlanCache {

    void addOrUpdatePlan(Plan plan);

    List<Plan> getAllPlans(int skip, int limit, boolean visible, boolean deleted);

}
