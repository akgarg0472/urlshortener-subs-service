package com.akgarg.subsservice.response;

import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GetPacksResponse(
        @JsonProperty("status_code") int statusCode,
        @JsonProperty("packs") List<SubscriptionPackDTO> packs,
        @JsonProperty("errors") Object errors) {
}
