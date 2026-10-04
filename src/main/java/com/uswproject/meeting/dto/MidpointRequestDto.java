package com.uswproject.meeting.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MidpointRequestDto(
        @NotNull(message = "참가자 위치 목록은 필수입니다.")
        @Size(min = 2, max = 20, message = "참가자는 2명 이상 20명 이하여야 합니다.")
        List<@NotNull(message = "참가자 위치는 null일 수 없습니다.") @Valid LocationDto> locations
) {
}
