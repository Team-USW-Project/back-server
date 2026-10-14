package com.uswproject.meeting;

import com.uswproject.kakao.client.KakaoRoutingClient;
import com.uswproject.kakao.exception.KakaoApiException;
import com.uswproject.meeting.dto.*;
import com.uswproject.meeting.service.TravelRouteService;
import com.uswproject.meeting.controller.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TravelRouteTests {
    @Test void partialFailurePreservesParticipantOrder() {
        var client = mock(KakaoRoutingClient.class);
        when(client.route(eq(0), any(), any())).thenThrow(new KakaoApiException(HttpStatus.BAD_GATEWAY,"ROUTE_UNAVAILABLE","다시 시도해주세요."));
        doReturn(new TravelRouteDto(1,"TRANSIT","OK","",1200,2000,1,List.of())).when(client).route(eq(1), any(), any());
        var p = new TravelRoutesRequestDto.Participant(new LocationDto(37.5,127.0),"TRANSIT");
        var result = new TravelRouteService(client).routes(new TravelRoutesRequestDto(List.of(p,p),new LocationDto(37.6,127.1)));
        assertEquals("ROUTE_UNAVAILABLE",result.get(0).status());
        assertNull(result.get(0).durationSeconds());
        assertEquals(1,result.get(1).participantIndex());
        assertEquals(1200,result.get(1).durationSeconds());
    }
    @Test void apiValidatesNestedCoordinatesTransportAndEmptyParticipants() throws Exception {
        var service = mock(TravelRouteService.class);
        when(service.routes(any())).thenReturn(List.of());
        var mvc = MockMvcBuilders.standaloneSetup(new TravelRouteController(service)).build();
        String valid = "{\"participants\":[{\"location\":{\"latitude\":37.5,\"longitude\":127},\"transportMode\":\"CAR\"}],\"destination\":{\"latitude\":37.6,\"longitude\":127.1}}";
        mvc.perform(post("/meetings/routes").contentType(MediaType.APPLICATION_JSON).content(valid)).andExpect(status().isOk());
        clearInvocations(service);
        for (String invalid : List.of(valid.replace("CAR","BOAT"),valid.replace("37.5","100"),
            "{\"participants\":[],\"destination\":{\"latitude\":37.6,\"longitude\":127.1}}", valid.replace("\"CAR\"","null"))) {
            mvc.perform(post("/meetings/routes").contentType(MediaType.APPLICATION_JSON).content(invalid)).andExpect(status().isBadRequest());
        }
        verifyNoInteractions(service);
    }
}
