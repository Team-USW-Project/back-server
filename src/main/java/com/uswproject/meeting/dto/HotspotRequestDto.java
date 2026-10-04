package com.uswproject.meeting.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record HotspotRequestDto(
        @NotNull @Size(min = 2, max = 20) List<@NotNull @Valid LocationDto> locations,
        @Pattern(regexp = "CAFE|RESTAURANT") String category,
        @Size(max = 40) String areaId
) {}
