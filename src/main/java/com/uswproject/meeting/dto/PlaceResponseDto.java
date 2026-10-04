package com.uswproject.meeting.dto;

public record PlaceResponseDto(
        String id,
        String name,
        String address,
        double latitude,
        double longitude,
        int distanceMeters,
        String placeUrl
) {
}
