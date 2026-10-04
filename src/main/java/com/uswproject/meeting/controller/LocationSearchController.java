package com.uswproject.meeting.controller;

import com.uswproject.kakao.client.KakaoLocalClient;
import com.uswproject.meeting.dto.LocationSearchResponseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationSearchController {
    private final KakaoLocalClient kakaoLocalClient;

    @GetMapping("/search")
    public List<LocationSearchResponseDto> search(
            @RequestParam("query") @NotBlank @Size(max = 100) String query) {
        return kakaoLocalClient.searchLocations(query.trim());
    }
}
