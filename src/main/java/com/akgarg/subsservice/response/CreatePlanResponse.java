package com.akgarg.subsservice.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePlanResponse(
        @JsonIgnore int statusCode,
        @JsonProperty("message") String message
) {
}
