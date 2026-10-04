package com.uswproject.meeting;

import com.uswproject.kakao.client.KakaoLocalClient;
import com.uswproject.kakao.exception.KakaoApiException;
import com.uswproject.meeting.controller.MeetingController;
import com.uswproject.meeting.controller.MeetingExceptionHandler;
import com.uswproject.meeting.dto.LocationDto;
import com.uswproject.meeting.dto.PlaceResponseDto;
import com.uswproject.meeting.service.MeetingService;
import com.uswproject.meeting.service.MidpointService;
import com.uswproject.meeting.service.HotspotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MeetingRecommendationTests {

    private KakaoLocalClient kakaoClient;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        kakaoClient = mock(KakaoLocalClient.class);
        MidpointService midpointService = new MidpointService();
        MeetingService service = new MeetingService(midpointService, kakaoClient);
        mockMvc = MockMvcBuilders.standaloneSetup(new MeetingController(midpointService, service, new HotspotService(midpointService, kakaoClient)))
                .setControllerAdvice(new MeetingExceptionHandler()).build();
    }

    @Test
    void returnsMidpointAndCafeList() throws Exception {
        when(kakaoClient.searchCafes(any(), eq(1000))).thenReturn(List.of(
                new PlaceResponseDto("1", "Cafe", "Address", 37.501, 127.001, 100,
                        "https://place.map.kakao.com/1")));
        mockMvc.perform(post("/meetings/recommendations/cafes").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"locations":[{"latitude":37,"longitude":126},
                                              {"latitude":38,"longitude":128}]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.midpoint.latitude").value(37.5))
                .andExpect(jsonPath("$.midpoint.longitude").value(127.0))
                .andExpect(jsonPath("$.radiusMeters").value(1000))
                .andExpect(jsonPath("$.places[0].name").value("Cafe"));
        verify(kakaoClient).searchCafes(new LocationDto(37.5, 127.0), 1000);
    }

    @Test
    void invalidInputNeverCallsKakao() throws Exception {
        mockMvc.perform(post("/meetings/recommendations/cafes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"locations\":[]}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(kakaoClient);
    }

    @Test
    void apiFailureReturnsErrorCodeAndMessage() throws Exception {
        when(kakaoClient.searchCafes(any(), eq(1000))).thenThrow(
                new KakaoApiException(HttpStatus.SERVICE_UNAVAILABLE, "KAKAO_UNAVAILABLE", "Try again"));
        mockMvc.perform(post("/meetings/recommendations/cafes").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"locations":[{"latitude":37,"longitude":126},
                                              {"latitude":38,"longitude":128}]}
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("KAKAO_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("Try again"));
    }
}
