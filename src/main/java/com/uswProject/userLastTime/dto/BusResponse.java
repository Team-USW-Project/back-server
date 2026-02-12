package com.uswProject.userLastTime.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BusResponse {

    private Result result;


    @NoArgsConstructor
    @Getter
    public static class Result {
        private int busID;
        private String busInterval;
        private String busLastTime;

        private List<station> station;
    }

    @NoArgsConstructor
    @Getter
    public static class station {
        private int idx;
        private int stationID;
    }



}
