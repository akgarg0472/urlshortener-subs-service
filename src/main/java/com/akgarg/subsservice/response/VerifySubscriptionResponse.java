package com.akgarg.subsservice.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record VerifySubscriptionResponse(
        @JsonIgnore int statusCode,
        @JsonProperty("message") String message,
        @JsonProperty("success") boolean success) {
}
