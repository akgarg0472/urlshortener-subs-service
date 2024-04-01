package com.akgarg.subsservice.response;

import com.akgarg.subsservice.subs.v1.SubscriptionDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MakeSubscriptionResponse(
        @JsonIgnore int statusCode,
        @JsonProperty("message") String message,
        @JsonProperty("subscription") SubscriptionDTO subscription
) {
}
