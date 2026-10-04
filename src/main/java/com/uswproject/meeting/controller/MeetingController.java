package com.uswproject.meeting.controller;

import com.uswproject.meeting.dto.LocationDto;
import com.uswproject.meeting.dto.MidpointRequestDto;
import com.uswproject.meeting.dto.MeetingRecommendResponseDto;
import com.uswproject.meeting.service.MeetingService;
import com.uswproject.meeting.service.MidpointService;
import com.uswproject.meeting.service.HotspotService;
import com.uswproject.meeting.dto.HotspotRequestDto;
import com.uswproject.meeting.dto.HotspotResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MidpointService midpointService;
    private final MeetingService meetingService;
    private final HotspotService hotspotService;

    @PostMapping("/recommendations/hotspots")
    public HotspotResponseDto recommendHotspots(@RequestBody @Valid HotspotRequestDto request) {
        return hotspotService.recommend(request);
    }

    @PostMapping("/midpoint")
    public LocationDto calculateMidpoint(@RequestBody @Valid MidpointRequestDto request) {
        return midpointService.calculate(request.locations());
    }

    @PostMapping("/recommendations/cafes")
    public MeetingRecommendResponseDto recommendCafes(@RequestBody @Valid MidpointRequestDto request) {
        return meetingService.recommendCafes(request.locations());
    }
}
