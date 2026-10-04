package com.uswproject.meeting.dto;

import java.util.List;

public record HotspotResponseDto(LocationDto midpoint, Area selectedArea, List<Area> areas,
                                 String category, int radiusMeters, List<PlaceResponseDto> places) {
    public record Area(String id, String name, String description, LocationDto location,
                       long averageDistanceMeters, long maxDistanceMeters) {}
}
