package com.akgarg.subsservice.response;

import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetSubscriptionPackResponse {

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("pack")
    private SubscriptionPackDTO pack;

    @JsonProperty("message")
    private String message;

}
