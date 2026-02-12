package com.uswProject.userLastTime.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SubwayResponse {

    private Result result;

    @Getter
    @NoArgsConstructor
    public static class Result {
        private WeekdaySchedule weekdaySchedule;
    }

    @Getter
    @NoArgsConstructor
    public static class WeekdaySchedule {
        private List<up> up;
        private List<down> down;
    }

    @Getter
    @NoArgsConstructor
    public static class up {
        private String departureTime;
        private String startStationName;
        private String endStationName;
    }

    @Getter
    @NoArgsConstructor
    public static class down {
        private String departureTime;
        private String startStationName;
        private String endStationName;
    }
}
