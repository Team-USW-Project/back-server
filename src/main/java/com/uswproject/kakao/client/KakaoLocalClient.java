package com.uswproject.kakao.client;

import com.uswproject.kakao.dto.KakaoPlaceSearchResponse;
import com.uswproject.kakao.dto.KakaoAddressSearchResponse;
import com.uswproject.kakao.exception.KakaoApiException;
import com.uswproject.meeting.dto.LocationDto;
import com.uswproject.meeting.dto.PlaceResponseDto;
import com.uswproject.meeting.dto.LocationSearchResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class KakaoLocalClient {

    private final RestClient restClient;
    private final String apiKey;

    @Autowired
    public KakaoLocalClient(@Value("${kakao.rest-api-key:}") String apiKey) {
        this(createRestClient(), apiKey);
    }

    // 테스트에서는 실제 카카오 호출 대신 가짜 HTTP 응답을 연결합니다.
    KakaoLocalClient(RestClient restClient, String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey.trim();
    }

    private static RestClient createRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(8));
        return RestClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .requestFactory(factory)
                .build();
    }

    public List<PlaceResponseDto> searchCafes(LocationDto midpoint, int radiusMeters) {
        return searchPlaces(midpoint, radiusMeters, "CE7");
    }

    public List<PlaceResponseDto> searchPlaces(LocationDto midpoint, int radiusMeters, String categoryCode) {
        if (apiKey.isBlank()) {
            throw new KakaoApiException(HttpStatus.SERVICE_UNAVAILABLE, "KAKAO_KEY_MISSING",
                    "카카오 REST API 키를 설정하고 서버를 다시 실행해주세요.");
        }

        try {
            KakaoPlaceSearchResponse response = restClient.get()
                    .uri(builder -> builder.path("/v2/local/search/category.json")
                            .queryParam("category_group_code", categoryCode)
                            .queryParam("x", midpoint.longitude())
                            .queryParam("y", midpoint.latitude())
                            .queryParam("radius", radiusMeters)
                            .queryParam("sort", "distance")
                            .queryParam("size", 10)
                            .build())
                    .header("Authorization", "KakaoAK " + apiKey)
                    .retrieve()
                    .body(KakaoPlaceSearchResponse.class);

            if (response == null || response.documents() == null) {
                throw invalidResponse();
            }

            List<PlaceResponseDto> places = new ArrayList<>();
            for (KakaoPlaceSearchResponse.Place place : response.documents()) {
                String address = place.roadAddress();
                if (address == null || address.isBlank()) {
                    address = place.address();
                }
                places.add(new PlaceResponseDto(
                        place.id(), place.name(), address,
                        Double.parseDouble(place.y()), Double.parseDouble(place.x()),
                        Integer.parseInt(place.distance()), place.placeUrl()));
            }
            return places;
        } catch (RestClientResponseException exception) {
            throw httpError(exception);
        } catch (RestClientException exception) {
            throw new KakaoApiException(HttpStatus.SERVICE_UNAVAILABLE, "KAKAO_UNAVAILABLE",
                    "카카오 장소 검색에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        } catch (NumberFormatException | NullPointerException exception) {
            throw invalidResponse();
        }
    }

    public List<LocationSearchResponseDto> searchLocations(String query) {
        if (apiKey.isBlank()) {
            throw new KakaoApiException(HttpStatus.SERVICE_UNAVAILABLE, "KAKAO_KEY_MISSING",
                    "카카오 REST API 키를 설정하고 서버를 다시 실행해주세요.");
        }
        try {
            KakaoPlaceSearchResponse response = restClient.get()
                    .uri(builder -> builder.path("/v2/local/search/keyword.json")
                            .queryParam("query", query).queryParam("size", 10).build())
                    .header("Authorization", "KakaoAK " + apiKey)
                    .retrieve().body(KakaoPlaceSearchResponse.class);
            if (response == null || response.documents() == null) throw invalidResponse();

            List<LocationSearchResponseDto> results = new ArrayList<>();
            for (KakaoPlaceSearchResponse.Place place : response.documents()) {
                String address = place.roadAddress();
                if (address == null || address.isBlank()) address = place.address();
                results.add(new LocationSearchResponseDto(place.id(), place.name(), address,
                        Double.parseDouble(place.y()), Double.parseDouble(place.x())));
            }
            // 장소명이 아닌 도로명·지번 주소는 주소 검색으로 다시 확인합니다.
            if (results.isEmpty()) {
                KakaoAddressSearchResponse addresses = restClient.get()
                        .uri(builder -> builder.path("/v2/local/search/address.json")
                                .queryParam("query", query).queryParam("size", 10).build())
                        .header("Authorization", "KakaoAK " + apiKey)
                        .retrieve().body(KakaoAddressSearchResponse.class);
                if (addresses == null || addresses.documents() == null) throw invalidResponse();
                for (KakaoAddressSearchResponse.Address address : addresses.documents()) {
                    results.add(new LocationSearchResponseDto("address:" + address.name(),
                            address.name(), address.name(), Double.parseDouble(address.y()),
                            Double.parseDouble(address.x())));
                }
            }
            return results;
        } catch (RestClientResponseException exception) {
            throw httpError(exception);
        } catch (RestClientException exception) {
            throw new KakaoApiException(HttpStatus.SERVICE_UNAVAILABLE, "KAKAO_UNAVAILABLE",
                    "카카오 위치 검색에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        } catch (NumberFormatException | NullPointerException exception) {
            throw invalidResponse();
        }
    }

    private KakaoApiException httpError(RestClientResponseException exception) {
        int status = exception.getStatusCode().value();
        if (status == 401 || status == 403) {
            return new KakaoApiException(HttpStatus.BAD_GATEWAY, "KAKAO_AUTH_ERROR",
                    "카카오 키 또는 API 사용 권한을 확인해주세요.");
        }
        if (status == 429) {
            return new KakaoApiException(HttpStatus.SERVICE_UNAVAILABLE, "KAKAO_RATE_LIMIT",
                    "카카오 API 호출 한도에 도달했습니다. 잠시 후 다시 시도해주세요.");
        }
        return new KakaoApiException(HttpStatus.BAD_GATEWAY, "KAKAO_API_ERROR",
                "카카오 장소 검색에 실패했습니다. 잠시 후 다시 시도해주세요.");
    }

    private KakaoApiException invalidResponse() {
        return new KakaoApiException(HttpStatus.BAD_GATEWAY, "KAKAO_INVALID_RESPONSE",
                "카카오 장소 검색 응답을 처리할 수 없습니다.");
    }
}
