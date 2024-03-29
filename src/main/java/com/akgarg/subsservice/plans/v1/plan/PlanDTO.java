package com.akgarg.subsservice.plans.v1.plan;

@SuppressWarnings("all")
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