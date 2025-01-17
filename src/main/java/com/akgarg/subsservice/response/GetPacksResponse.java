package com.akgarg.subsservice.response;

import com.akgarg.subsservice.v1.pack.PackComparison;
import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetPacksResponse {

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("packs")
    private List<SubscriptionPackDTO> packs;

    @JsonProperty("comparisons")
    private PackComparison comparisons;

    @JsonProperty("errors")
    private Object errors;

}
