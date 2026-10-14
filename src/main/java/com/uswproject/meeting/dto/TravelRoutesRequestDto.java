package com.uswproject.meeting.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record TravelRoutesRequestDto(
        @NotNull @Size(min = 1, max = 20) List<@NotNull @Valid Participant> participants,
        @NotNull @Valid LocationDto destination
) {
    public record Participant(@NotNull @Valid LocationDto location,
                              @NotNull @Pattern(regexp = "CAR|TRANSIT") String transportMode) {}
}
