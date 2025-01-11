package com.akgarg.subsservice.response;

import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePackResponse(
        @JsonIgnore int statusCode,
        @JsonProperty("message") String message,
        @JsonProperty("pack") SubscriptionPackDTO pack
) {
}
