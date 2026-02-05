package com.uswProject.route.dto;

import java.util.List;

public class RouteResponse {
    private Result result;

    public class Result {
        private Integer searchType;      // 0: 도시내, 1: 도시간
        private Integer outTrafficType;  // 도시간 교통 수단
        private List<Path> path;         // 추천 경로 리스트
    }

    public class Path {
        private Integer pathType;        // 1:지하철, 2:버스, 3:버스+지하철
        private PathInfo info;           // 경로 요약 정보
        private List<SubPath> subPath;   // 상세 이동 구간 (도보, 버스, 지하철)
    }

    public class PathInfo {
        private Integer trafficDistance; // 총 대중교통 이동거리
        private Integer totalTime;       // 총 소요시간
        private Integer totalWalk;       // 총 도보 시간
        private Integer totalCharge;     // 총 요금
        private String mapObj;           // 지도 표시용 객체 (그래픽 데이터)
        private Integer busTransitCount; // 버스 환승 횟수
        private Integer subwayTransitCount; // 지하철 환승 횟수
    }

    public class SubPath {
        private Integer trafficType;     // 1:지하철, 2:버스, 3:도보
        private Integer distance;        // 이동 거리
        private Integer sectionTime;     // 소요 시간

        // trafficType이 1, 2일 때 포함되는 상세 정보 (버스/지하철)
        private String lane;             // 노선 정보 (버스번호, 지하철 노선명 등)
        private String startName;        // 승차 정류장/역명
        private String endName;          // 하차 정류장/역명

        // trafficType이 3(도보)일 때 포함
        private List<Object> doorToDoor; // 도보 경로 상세
    }
}
