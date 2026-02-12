package com.uswProject.userLastTime.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SummariesPathRequest {
    private double distance; // 이동 거리
    private String startName; // 승차역
    private String endName; // 하차역
    private int sectionTime; // 이동 시간
    private int startID; // 출발 정류장 ID (지하철 버스 포함)
    private int endID;
    private int trafficType;
    private int wayCode;

    private List<Lane> lane;

    @Getter
    @NoArgsConstructor
    public static class Lane {
        private int busID;
    }
}
