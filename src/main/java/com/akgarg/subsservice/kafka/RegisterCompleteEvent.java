package com.akgarg.subsservice.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterCompleteEvent(
        @JsonProperty("user_id") String userId
) {
}
