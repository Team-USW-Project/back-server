package com.uswproject.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAddressSearchResponse(List<Address> documents) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Address(@JsonProperty("address_name") String name, String x, String y) {
    }
}
