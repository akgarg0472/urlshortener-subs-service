package com.akgarg.subsservice.plans.v1.plan;

final class PlanMapper {

    private PlanMapper() {
        throw new IllegalStateException("utility class");
    }

    static PlanDTO toDto(final Plan plan) {
        return new PlanDTO(
                plan.getIcon(),
                plan.getId(),
                plan.getTitle(),
                plan.getDescription(),
                plan.getCode(),
                plan.getPrice(),
                plan.getFeatures()
        );
    }

}
