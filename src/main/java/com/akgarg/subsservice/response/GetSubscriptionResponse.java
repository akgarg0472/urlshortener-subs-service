package com.akgarg.subsservice.response;

import com.akgarg.subsservice.v1.subs.SubscriptionDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GetSubscriptionResponse(
        @JsonIgnore int statusCode,
        @JsonProperty("message") String message,
        @JsonProperty("subscription") SubscriptionDTO subscription) {
}
