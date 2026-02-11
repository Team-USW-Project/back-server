package com.uswProject.route.dto.odsay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OdsaySubPath {
    private int trafficType;
    private double distance;
    private int sectionTime;

    private String StartName;
    private double startX;
    private double startY;

    private String endName;
    private double endX;
    private double endY;

    private String way;

    private List<OdsayLane> lane;
}
