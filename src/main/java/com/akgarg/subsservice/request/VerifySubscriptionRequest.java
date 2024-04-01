package com.akgarg.subsservice.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VerifySubscriptionRequest(

        @JsonProperty("user_id")
        String userId,

        @JsonProperty("plan_id")
        String planId,

        @JsonProperty("subs_id")
        String subsId
) {
}
