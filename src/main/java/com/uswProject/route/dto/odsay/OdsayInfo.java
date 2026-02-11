package com.uswProject.route.dto.odsay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OdsayInfo {
    private int totalTime;
    private double totalDistance;
    private int busTransitCount;
    private int subwayTransitCount;
    private int payment;
    private int totalWalk;
}
