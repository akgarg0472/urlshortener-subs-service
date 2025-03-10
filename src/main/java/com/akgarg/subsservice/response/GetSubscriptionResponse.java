package com.akgarg.subsservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class GetSubscriptionResponse {

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("subscription")
    private GetSubscriptionResponseSubscription subscription;

    @JsonProperty("pack")
    private GetSubscriptionResponsePack pack;

}
