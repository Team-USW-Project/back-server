package com.uswproject.meeting.service;

import com.uswproject.meeting.dto.LocationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MidpointService {

    // 인근 지역 참가자들의 평균 좌표. 이동시간이나 도로 접근성은 반영하지 않습니다.
    public LocationDto calculate(List<LocationDto> locations) {
        double latitudeSum = 0;
        double longitudeSum = 0;

        for (LocationDto location : locations) {
            latitudeSum += location.latitude();
            longitudeSum += location.longitude();
        }

        int participantCount = locations.size();
        return new LocationDto(latitudeSum / participantCount, longitudeSum / participantCount);
    }
}
