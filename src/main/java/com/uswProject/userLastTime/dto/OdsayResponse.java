package com.uswProject.userLastTime.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class OdsayResponse {

    private Result result;

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Result {
        private List<Path> path;
    }

    @Getter
    @NoArgsConstructor
    @ToString
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
        private int sectionTime; // 이동 시간
        private int totalTime; // 총 이동시간
    }

    @Getter
    @NoArgsConstructor
    @ToString
    public static class SubPath {

        private double distance; // 이동 거리
        private String startName; // 승차역
        private String endName; // 하차역
    }


}
