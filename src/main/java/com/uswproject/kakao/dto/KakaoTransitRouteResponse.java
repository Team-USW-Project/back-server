package com.uswproject.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoTransitRouteResponse(String status, List<Route> routes) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Route(Properties properties, List<Step> steps) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Properties(Integer totalTime, Integer totalDistance, Integer transfers) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Step(StepProperties properties, Path path) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StepProperties(String type) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Path(List<List<Double>> points) {}
}
