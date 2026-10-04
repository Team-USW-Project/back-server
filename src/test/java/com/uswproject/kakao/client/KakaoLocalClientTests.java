package com.uswproject.kakao.client;

import com.uswproject.kakao.exception.KakaoApiException;
import com.uswproject.meeting.dto.LocationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KakaoLocalClientTests {

    private MockRestServiceServer server;
    private KakaoLocalClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://dapi.kakao.com");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new KakaoLocalClient(builder.build(), "test-key");
    }

    @Test
    void sendsCoordinatesAndMapsKakaoResponse() {
        server.expect(queryParam("category_group_code", "CE7"))
                .andExpect(queryParam("x", "127.02"))
                .andExpect(queryParam("y", "37.53"))
                .andExpect(queryParam("radius", "1000"))
                .andExpect(queryParam("sort", "distance"))
                .andExpect(queryParam("size", "10"))
                .andExpect(header("Authorization", "KakaoAK test-key"))
                .andRespond(withSuccess("""
                        {"meta":{"total_count":2},"documents":[
                          {"id":"1","place_name":"Test cafe","address_name":"Old address",
                           "road_address_name":"Road address","x":"127.021","y":"37.531",
                           "distance":"100","place_url":"https://place.map.kakao.com/1"},
                          {"id":"2","place_name":"Second cafe","address_name":"Fallback address",
                           "road_address_name":"","x":"127.022","y":"37.532",
                           "distance":"200","place_url":"https://place.map.kakao.com/2"}
                        ]}
                        """, MediaType.APPLICATION_JSON));

        var places = client.searchCafes(new LocationDto(37.53, 127.02), 1000);

        assertEquals(2, places.size());
        assertEquals("Test cafe", places.get(0).name());
        assertEquals("Road address", places.get(0).address());
        assertEquals(37.531, places.get(0).latitude(), 1e-9);
        assertEquals(127.021, places.get(0).longitude(), 1e-9);
        assertEquals(100, places.get(0).distanceMeters());
        assertEquals("Fallback address", places.get(1).address());
        server.verify();
    }

    @Test
    void emptySearchIsSuccessful() {
        server.expect(queryParam("category_group_code", "CE7"))
                .andRespond(withSuccess("{\"documents\":[]}", MediaType.APPLICATION_JSON));
        assertTrue(client.searchCafes(new LocationDto(37.53, 127.02), 1000).isEmpty());
        server.verify();
    }

    @Test
    void missingKeyFailsWithoutRequest() {
        var withoutKey = new KakaoLocalClient(RestClient.builder().build(), " ");
        var exception = assertThrows(KakaoApiException.class,
                () -> withoutKey.searchCafes(new LocationDto(37.53, 127.02), 1000));
        assertEquals("KAKAO_KEY_MISSING", exception.getCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void authenticationFailureReturnsSafeMessage() {
        server.expect(queryParam("category_group_code", "CE7"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED).body("private upstream details"));
        var exception = assertThrows(KakaoApiException.class,
                () -> client.searchCafes(new LocationDto(37.53, 127.02), 1000));
        assertEquals("KAKAO_AUTH_ERROR", exception.getCode());
        assertFalse(exception.getMessage().contains("private upstream details"));
        server.verify();
    }

    @Test
    void rateLimitIsReportedSeparately() {
        server.expect(queryParam("category_group_code", "CE7"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        var exception = assertThrows(KakaoApiException.class,
                () -> client.searchCafes(new LocationDto(37.53, 127.02), 1000));
        assertEquals("KAKAO_RATE_LIMIT", exception.getCode());
        server.verify();
    }

    @Test
    void keywordSearchReturnsNameAndCoordinatesWithoutDistance() {
        server.expect(queryParam("size", "10"))
                .andExpect(request -> {
                    assertEquals("/v2/local/search/keyword.json", request.getURI().getPath());
                    String query = org.springframework.web.util.UriComponentsBuilder.fromUri(request.getURI())
                            .build().getQueryParams().getFirst("query");
                    assertEquals("수원역", org.springframework.web.util.UriUtils.decode(query, java.nio.charset.StandardCharsets.UTF_8));
                })
                .andRespond(withSuccess("""
                        {"documents":[{"id":"station","place_name":"수원역","address_name":"수원시",
                        "road_address_name":"","x":"127.0","y":"37.26","distance":""}]}
                        """, MediaType.APPLICATION_JSON));
        var results = client.searchLocations("수원역");
        assertEquals("수원역", results.get(0).name());
        assertEquals("수원시", results.get(0).address());
        assertEquals(37.26, results.get(0).latitude(), 1e-9);
        server.verify();
    }

    @Test
    void emptyKeywordSearchFallsBackToAddress() {
        server.expect(request -> assertEquals("/v2/local/search/keyword.json", request.getURI().getPath()))
                .andRespond(withSuccess("{\"documents\":[]}", MediaType.APPLICATION_JSON));
        server.expect(request -> assertEquals("/v2/local/search/address.json", request.getURI().getPath()))
                .andRespond(withSuccess("""
                        {"documents":[{"address_name":"서울시 도로명 주소","x":"127.01","y":"37.51"}]}
                        """, MediaType.APPLICATION_JSON));
        var results = client.searchLocations("서울시 도로명 주소");
        assertEquals("서울시 도로명 주소", results.get(0).name());
        assertEquals(127.01, results.get(0).longitude(), 1e-9);
        server.verify();
    }

    @Test
    void noPlaceOrAddressReturnsEmptyList() {
        server.expect(request -> {}).andRespond(withSuccess("{\"documents\":[]}", MediaType.APPLICATION_JSON));
        server.expect(request -> {}).andRespond(withSuccess("{\"documents\":[]}", MediaType.APPLICATION_JSON));
        assertTrue(client.searchLocations("no-match").isEmpty());
        server.verify();
    }
}
