package com.uswproject.kakao.client;

import com.uswproject.kakao.exception.KakaoApiException;
import com.uswproject.meeting.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class KakaoRoutingClientTests {
    MockRestServiceServer carServer, transitServer;
    KakaoRoutingClient client;
    LocationDto origin = new LocationDto(37.501, 127.019);
    LocationDto destination = new LocationDto(37.4979, 127.0276);
    @BeforeEach void setup() {
        var car = RestClient.builder().baseUrl("https://car.example");
        var transit = RestClient.builder().baseUrl("https://transit.example");
        carServer = MockRestServiceServer.bindTo(car).build();
        transitServer = MockRestServiceServer.bindTo(transit).build();
        client = new KakaoRoutingClient(car.build(), transit.build(), "test-key");
    }
    TravelRoutesRequestDto.Participant participant(String mode) { return new TravelRoutesRequestDto.Participant(origin, mode); }
    @Test void carUsesLongitudeFirstAndConvertsSecondsAndGeometry() {
        carServer.expect(queryParam("origin", "127.019,37.501"))
            .andExpect(queryParam("destination", "127.0276,37.4979"))
            .andExpect(header("Authorization", "KakaoAK test-key"))
            .andRespond(withSuccess("""
                {"routes":[{"result_code":0,"summary":{"duration":441,"distance":1836},
                "sections":[{"roads":[{"vertexes":[127.019,37.501,127.0276,37.4979]}]}]}]}
                """, MediaType.APPLICATION_JSON));
        var result = client.route(2, participant("CAR"), destination);
        assertEquals(2, result.participantIndex());
        assertEquals(441, result.durationSeconds());
        assertEquals(1836, result.distanceMeters());
        assertNull(result.transfers());
        assertEquals(origin, result.segments().get(0).points().get(0));
        assertEquals(destination, result.segments().get(0).points().get(1));
        carServer.verify();
    }
    @Test void transitSelectsFastestRouteAndIncludesWalkingAndTransfers() {
        transitServer.expect(queryParam("start_x", "127.019"))
            .andExpect(queryParam("start_y", "37.501"))
            .andRespond(withSuccess("""
              {"status":"OK","routes":[
                {"properties":{"totalTime":2400,"totalDistance":5000,"transfers":2}},
                {"properties":{"totalTime":1091,"totalDistance":1672,"transfers":1},
                 "steps":[{"properties":{"type":"WALKING"},"path":{"points":[[127.019,37.501],[127.02,37.5]]}},
                 {"properties":{"type":"SUBWAY"},"path":{"points":[[127.02,37.5],[127.0276,37.4979]]}}]}]}
              """, MediaType.APPLICATION_JSON));
        var result = client.route(0, participant("TRANSIT"), destination);
        assertEquals(1091, result.durationSeconds());
        assertEquals(1, result.transfers());
        assertEquals("WALKING", result.segments().get(0).type());
        assertEquals("SUBWAY", result.segments().get(1).type());
        assertEquals(destination, result.segments().get(1).points().get(1));
        transitServer.verify();
    }
    @Test void noRouteDoesNotPretendToTakeZeroMinutes() {
        transitServer.expect(request -> {}).andRespond(withSuccess("{\"status\":\"NO_RESULTS\"}", MediaType.APPLICATION_JSON));
        var result = client.route(0, participant("TRANSIT"), destination);
        assertEquals("NO_ROUTE", result.status());
        assertNull(result.durationSeconds());
        assertTrue(result.segments().isEmpty());
    }
    @Test void upstreamAuthFailureDoesNotExposeBody() {
        carServer.expect(request -> {}).andRespond(withStatus(HttpStatus.FORBIDDEN).body("secret upstream details"));
        var error = assertThrows(KakaoApiException.class, () -> client.route(0, participant("CAR"), destination));
        assertEquals("ROUTE_AUTH_ERROR", error.getCode());
        assertFalse(error.getMessage().contains("secret"));
    }
    @Test void invalidGeometryIsRejected() {
        carServer.expect(request -> {}).andRespond(withSuccess("""
            {"routes":[{"result_code":0,"summary":{"duration":1,"distance":1},
            "sections":[{"roads":[{"vertexes":[999,37.5,127,37.5]}]}]}]}
            """, MediaType.APPLICATION_JSON));
        assertEquals("ROUTE_INVALID_RESPONSE", assertThrows(KakaoApiException.class,
            () -> client.route(0, participant("CAR"), destination)).getCode());
    }
    @Test void sameDestinationNeedsNoExternalCall() {
        var result = client.route(0, participant("TRANSIT"), origin);
        assertEquals("OK", result.status());
        assertEquals(0, result.durationSeconds());
        assertEquals(0, result.transfers());
        carServer.verify(); transitServer.verify();
    }
    @Test void missingGeometryKeepsTimeWithoutInventingLine() {
        carServer.expect(request -> {}).andRespond(withSuccess("{\"routes\":[{\"result_code\":0,\"summary\":{\"duration\":441,\"distance\":1836}}]}", MediaType.APPLICATION_JSON));
        var result = client.route(0, participant("CAR"), destination);
        assertEquals(441, result.durationSeconds());
        assertTrue(result.segments().isEmpty());
        assertFalse(result.message().isBlank());
    }
}
