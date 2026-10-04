package com.uswproject.meeting;

import com.uswproject.meeting.controller.MeetingController;
import com.uswproject.meeting.dto.LocationDto;
import com.uswproject.meeting.service.MidpointService;
import com.uswproject.meeting.service.HotspotService;
import com.uswproject.meeting.service.MeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.closeTo;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MidpointTests {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new MeetingController(new MidpointService(), mock(MeetingService.class), mock(HotspotService.class))).build();
    }

    @Test
    void averagesAllParticipants() {
        var result = new MidpointService().calculate(List.of(
                new LocationDto(36.0, 126.0),
                new LocationDto(37.0, 127.0),
                new LocationDto(38.0, 128.0)));
        assertEquals(37.0, result.latitude(), 1e-9);
        assertEquals(127.0, result.longitude(), 1e-9);
    }

    @Test
    void returnsMidpointThroughApi() throws Exception {
        mockMvc.perform(post("/meetings/midpoint").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"locations":[{"latitude":37.5,"longitude":127.0},
                                              {"latitude":37.56,"longitude":127.04}]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(closeTo(37.53, 1e-9)))
                .andExpect(jsonPath("$.longitude").value(closeTo(127.02, 1e-9)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"locations\":[]}",
            "{\"locations\":[{\"latitude\":37,\"longitude\":127}]}",
            "{\"locations\":[null,{\"latitude\":37,\"longitude\":127}]}",
            "{\"locations\":[{\"longitude\":127},{\"latitude\":37,\"longitude\":127}]}",
            "{\"locations\":[{\"latitude\":91,\"longitude\":127},{\"latitude\":37,\"longitude\":127}]}",
            "{\"locations\":[{\"latitude\":37,\"longitude\":-181},{\"latitude\":37,\"longitude\":127}]}"
    })
    void rejectsInvalidLocations(String body) throws Exception {
        mockMvc.perform(post("/meetings/midpoint").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
