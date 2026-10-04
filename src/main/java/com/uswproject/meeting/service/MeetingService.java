package com.uswproject.meeting.service;

import com.uswproject.kakao.client.KakaoLocalClient;
import com.uswproject.meeting.dto.LocationDto;
import com.uswproject.meeting.dto.MeetingRecommendResponseDto;
import com.uswproject.meeting.dto.PlaceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private static final int CAFE_SEARCH_RADIUS_METERS = 1000;

    private final MidpointService midpointService;
    private final KakaoLocalClient kakaoLocalClient;

    public MeetingRecommendResponseDto recommendCafes(List<LocationDto> locations) {
        LocationDto midpoint = midpointService.calculate(locations);
        List<PlaceResponseDto> places = kakaoLocalClient.searchCafes(midpoint, CAFE_SEARCH_RADIUS_METERS);
        return new MeetingRecommendResponseDto(midpoint, CAFE_SEARCH_RADIUS_METERS, places);
    }
}
