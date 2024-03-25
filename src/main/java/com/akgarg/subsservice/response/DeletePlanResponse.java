package com.akgarg.subsservice.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DeletePlanResponse(
        @JsonIgnore int statusCode,
        @JsonProperty("message") String message
) {
}