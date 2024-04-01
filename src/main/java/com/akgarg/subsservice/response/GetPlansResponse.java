package com.akgarg.subsservice.response;

import com.akgarg.subsservice.v1.plans.plan.PlanDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GetPlansResponse(
        @JsonProperty("status_code") int statusCode,
        @JsonProperty("plans") List<PlanDTO> plans,
        @JsonProperty("errors") Object errors) {
}
