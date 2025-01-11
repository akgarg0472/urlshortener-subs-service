package com.akgarg.subsservice.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePackRequest(

        @JsonProperty("id")
        @NotBlank(message = "Please provide valid and unique plan id")
        String id,

        @JsonProperty("name")
        @NotBlank(message = "Please provide valid plan name")
        String name,

        @JsonProperty("validity")
        @NotBlank(message = "Please provide valid plan validity ('month' or 'annual')")
        String validity,

        @JsonProperty("features")
        @NotNull(message = "Please provide valid plan features")
        List<String> features,

        @JsonProperty("privileges")
        @NotNull(message = "Please provide valid plan privileges")
        List<String> privileges,

        @JsonProperty("description")
        @NotBlank(message = "Please provide valid plan description")
        String description,

        @JsonProperty("price")
        @NotNull(message = "Please provide valid plan price")
        @Min(value = 1, message = "Price should be minimum 1")
        Double price,

        @JsonProperty("currency")
        @NotBlank(message = "Please provide valid plan currency")
        String currency,

        @JsonProperty("visible")
        Boolean visible,

        @JsonProperty("selected")
        Boolean selected

) {
    public CreatePackRequest {
        if (visible == null) {
            visible = Boolean.TRUE;
        }

        if (selected == null) {
            selected = Boolean.FALSE;
        }
    }
}
