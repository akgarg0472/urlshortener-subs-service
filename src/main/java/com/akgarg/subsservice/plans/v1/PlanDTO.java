package com.akgarg.subsservice.plans.v1;

public record PlanDTO(
        String icon,
        String id,
        String title,
        String description,
        String code,
        Double price,
        String[] features
) {
}
