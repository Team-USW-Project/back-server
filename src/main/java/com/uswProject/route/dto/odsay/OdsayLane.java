package com.uswProject.route.dto.odsay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OdsayLane {
    private String name;
    private String busNo;
    private Integer subwayCode;
    private Integer busID;
}
