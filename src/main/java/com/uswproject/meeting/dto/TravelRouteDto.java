package com.uswproject.meeting.dto;

import java.util.List;

public record TravelRouteDto(int participantIndex, String transportMode, String status, String message,
                             Integer durationSeconds, Integer distanceMeters, Integer transfers,
                             List<Segment> segments) {
    public record Segment(String type, List<LocationDto> points) {}
    public static TravelRouteDto failure(int index, String mode, String code, String message) {
        return new TravelRouteDto(index, mode, code, message, null, null, null, List.of());
    }
}
