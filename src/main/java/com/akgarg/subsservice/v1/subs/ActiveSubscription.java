package com.akgarg.subsservice.v1.subs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ActiveSubscription(
        @JsonProperty("user_id") String userId,
        @JsonProperty("pack_id") String packId,
        @JsonProperty("expires_at") long expiresAt
) {
}
