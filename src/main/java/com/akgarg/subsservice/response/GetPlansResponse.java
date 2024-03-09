package com.akgarg.subsservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetPlansResponse(@JsonProperty("status_code") int statusCode,
                               @JsonProperty("message") String message,
                               @JsonProperty("errors") Object errors) {
}
