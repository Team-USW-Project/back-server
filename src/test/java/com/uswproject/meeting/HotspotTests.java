package com.uswproject.meeting;

import com.uswproject.kakao.client.KakaoLocalClient;
import com.uswproject.kakao.exception.KakaoApiException;
import com.uswproject.meeting.dto.*;
import com.uswproject.meeting.service.*;
import com.uswproject.meeting.controller.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HotspotTests {
    KakaoLocalClient client;
    HotspotService service;
    MockMvc mvc;
    List<LocationDto> suwon = List.of(new LocationDto(37.266,126.998),new LocationDto(37.265,127.001));
    @BeforeEach void setup() {
        client=mock(KakaoLocalClient.class);
        var midpoint=new MidpointService();
        service=new HotspotService(midpoint,client);
        when(client.searchPlaces(any(),eq(500),anyString())).thenReturn(List.of());
        mvc=MockMvcBuilders.standaloneSetup(new MeetingController(midpoint,new MeetingService(midpoint,client),service))
                .setControllerAdvice(new MeetingExceptionHandler()).build();
    }
    @Test void recommendsSuwonStationInsteadOfRawMidpoint() {
        var result=service.recommend(new HotspotRequestDto(suwon,"CAFE",null));
        assertEquals("suwon",result.selectedArea().id());
        assertEquals(3,result.areas().size());
        assertNotEquals(result.midpoint(),result.selectedArea().location());
        verify(client).searchPlaces(result.selectedArea().location(),500,"CE7");
        for (int i=1;i<3;i++) {
            var previous=result.areas().get(i-1); var next=result.areas().get(i);
            assertTrue(previous.maxDistanceMeters()+previous.averageDistanceMeters()*.25 <= next.maxDistanceMeters()+next.averageDistanceMeters()*.25);
        }
    }
    @Test void selectedAlternativeAndRestaurantsChangeSearchCenterAndCategory() {
        var first=service.recommend(new HotspotRequestDto(suwon,null,null));
        var alternative=first.areas().get(1);
        var result=service.recommend(new HotspotRequestDto(suwon,"RESTAURANT",alternative.id()));
        assertEquals(alternative.id(),result.selectedArea().id());
        verify(client).searchPlaces(alternative.location(),500,"FD6");
    }
    @Test void outsideRegionIsRejectedWithoutPlaceSearch() {
        var error=assertThrows(KakaoApiException.class,()->service.recommend(new HotspotRequestDto(
            List.of(new LocationDto(35.16,129.06),new LocationDto(35.17,129.07)),"CAFE",null)));
        assertEquals("AREA_NOT_SUPPORTED",error.getCode());
        verifyNoInteractions(client);
    }
    @Test void unknownAreaIsRejected() {
        assertThrows(KakaoApiException.class,()->service.recommend(new HotspotRequestDto(suwon,"CAFE","unknown")));
        verifyNoInteractions(client);
    }
    @Test void apiReturnsAreaAndRejectsInvalidCategory() throws Exception {
        mvc.perform(post("/meetings/recommendations/hotspots").contentType(MediaType.APPLICATION_JSON)
            .content("{\"locations\":[{\"latitude\":37.266,\"longitude\":126.998},{\"latitude\":37.265,\"longitude\":127.001}],\"category\":\"RESTAURANT\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.selectedArea.id").value("suwon"))
            .andExpect(jsonPath("$.category").value("RESTAURANT")).andExpect(jsonPath("$.radiusMeters").value(500));
        clearInvocations(client);
        mvc.perform(post("/meetings/recommendations/hotspots").contentType(MediaType.APPLICATION_JSON)
            .content("{\"locations\":[{\"latitude\":37.266,\"longitude\":126.998},{\"latitude\":37.265,\"longitude\":127.001}],\"category\":\"OTHER\"}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(client);
    }
}
