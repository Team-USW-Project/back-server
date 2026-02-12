package com.uswProject.userLastTime.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OdsayResponse {

    private Result result;

    @Getter
    @NoArgsConstructor
    public static class Result {
        private List<Path> path;
    }

    @Getter
    @NoArgsConstructor
    public static class Path {
        private int pathType; // 이동 수단 종류
        private Info info;
        private List<SubPath> subPath; // 세부 경로
    }

    @Getter
    @NoArgsConstructor
    public static class Info {
        private String firstStartStation; // 최초 출발역
        private String lastEndStation; // 최종 도착역
        private int totalTime; // 총 이동시간
    }

    @Getter
    @NoArgsConstructor
    public static class SubPath {

        private double distance; // 이동 거리
        private String startName; // 승차역
        private String endName; // 하차역
        private int sectionTime; // 이동 시간
        private int startID; // 출발 정류장 ID (지하철 버스 포함)
        private int endID;
        private int trafficType;
        private int wayCode;
        private List<Lane> lane;
    }

    @Getter
    @NoArgsConstructor
    public static class Lane {
        private int busID;
    }


}
