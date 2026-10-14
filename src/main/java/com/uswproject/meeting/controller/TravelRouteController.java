package com.uswproject.meeting.controller;

import com.uswproject.meeting.dto.*;
import com.uswproject.meeting.service.TravelRouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class TravelRouteController {
    private final TravelRouteService service;
    @PostMapping("/routes")
    public List<TravelRouteDto> routes(@RequestBody @Valid TravelRoutesRequestDto request) {
        return service.routes(request);
    }
}
