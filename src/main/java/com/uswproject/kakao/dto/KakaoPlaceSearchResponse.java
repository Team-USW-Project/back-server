package com.uswproject.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoPlaceSearchResponse(List<Place> documents) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Place(
            String id,
            @JsonProperty("place_name") String name,
            @JsonProperty("address_name") String address,
            @JsonProperty("road_address_name") String roadAddress,
            String x,
            String y,
            String distance,
            @JsonProperty("place_url") String placeUrl
    ) {
    }
}
