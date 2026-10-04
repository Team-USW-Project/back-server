package com.uswproject.meeting.dto;

public record LocationSearchResponseDto(
        String id,
        String name,
        String address,
        double latitude,
        double longitude
) {
}
