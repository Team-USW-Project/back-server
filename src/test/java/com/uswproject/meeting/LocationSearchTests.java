package com.uswproject.meeting;

import com.uswproject.kakao.client.KakaoLocalClient;
import com.uswproject.meeting.controller.LocationSearchController;
import com.uswproject.meeting.controller.MeetingExceptionHandler;
import com.uswproject.meeting.dto.LocationSearchResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LocationSearchTests {
    private KakaoLocalClient client;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        client = mock(KakaoLocalClient.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new LocationSearchController(client))
                .setControllerAdvice(new MeetingExceptionHandler()).build();
    }

    @Test
    void searchesTrimmedQuery() throws Exception {
        when(client.searchLocations("수원역")).thenReturn(List.of(
                new LocationSearchResponseDto("1", "수원역", "수원시", 37.26, 127.0)));
        mockMvc.perform(get("/locations/search").param("query", " 수원역 "))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("수원역"));
        verify(client).searchLocations("수원역");
    }

    @Test
    void rejectsBlankQuery() throws Exception {
        mockMvc.perform(get("/locations/search").param("query", " ")).andExpect(status().isBadRequest());
        verifyNoInteractions(client);
    }

    @Test
    void rejectsTooLongQuery() throws Exception {
        mockMvc.perform(get("/locations/search").param("query", "a".repeat(101))).andExpect(status().isBadRequest());
        verifyNoInteractions(client);
    }
}
