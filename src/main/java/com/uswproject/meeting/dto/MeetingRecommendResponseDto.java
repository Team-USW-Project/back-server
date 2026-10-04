package com.uswproject.meeting.dto;

import java.util.List;

public record MeetingRecommendResponseDto(
        LocationDto midpoint,
        int radiusMeters,
        List<PlaceResponseDto> places
) {
}
