package com.uswproject.kakao.client;

import com.uswproject.kakao.dto.*;
import com.uswproject.kakao.exception.KakaoApiException;
import com.uswproject.meeting.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class KakaoRoutingClient {
    private final RestClient carClient;
    private final RestClient transitClient;
    private final String apiKey;

    @Autowired
    public KakaoRoutingClient(@Value("${kakao.rest-api-key:}") String apiKey) {
        this(client("https://apis-navi.kakaomobility.com"), client("https://dapi.kakao.com"), apiKey);
    }
    KakaoRoutingClient(RestClient carClient, RestClient transitClient, String apiKey) {
        this.carClient = carClient;
        this.transitClient = transitClient;
        this.apiKey = apiKey.trim();
    }
    private static RestClient client(String baseUrl) {
        var factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build());
        factory.setReadTimeout(Duration.ofSeconds(8));
        return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    public TravelRouteDto route(int index, TravelRoutesRequestDto.Participant participant, LocationDto destination) {
        String mode = participant.transportMode();
        if (apiKey.isBlank()) throw error("ROUTE_KEY_MISSING", "카카오 REST API 키 설정을 확인해주세요.");
        if (participant.location().equals(destination)) {
            return new TravelRouteDto(index, mode, "OK", "출발지와 목적지가 같습니다.", 0, 0,
                    mode.equals("TRANSIT") ? 0 : null, List.of());
        }
        try {
            return mode.equals("CAR") ? car(index, participant.location(), destination) : transit(index, participant.location(), destination);
        } catch (RestClientResponseException exception) {
            int status = exception.getStatusCode().value();
            if (status == 401 || status == 403) throw error("ROUTE_AUTH_ERROR", "경로 API 키 또는 사용 권한을 확인해주세요.");
            if (status == 429) throw error("ROUTE_RATE_LIMIT", "경로 API 호출 한도에 도달했습니다. 잠시 후 다시 시도해주세요.");
            throw error("ROUTE_API_ERROR", "경로 API가 요청을 처리하지 못했습니다.");
        } catch (RestClientException exception) {
            throw error("ROUTE_UNAVAILABLE", "경로 조회 연결이 지연되거나 실패했습니다. 다시 시도해주세요.");
        } catch (NullPointerException | IllegalArgumentException exception) {
            throw error("ROUTE_INVALID_RESPONSE", "경로 응답을 처리할 수 없습니다.");
        }
    }

    private TravelRouteDto car(int index, LocationDto origin, LocationDto destination) {
        var response = carClient.get().uri(builder -> builder.path("/v1/directions")
                .queryParam("origin", origin.longitude() + "," + origin.latitude())
                .queryParam("destination", destination.longitude() + "," + destination.latitude())
                .queryParam("priority", "TIME").queryParam("summary", false).build())
                .header("Authorization", "KakaoAK " + apiKey).retrieve().body(KakaoCarRouteResponse.class);
        if (response == null || response.routes() == null) throw invalid();
        var route = response.routes().stream().filter(r -> r.resultCode() != null && r.resultCode() == 0)
                .findFirst().orElse(null);
        if (route == null) return TravelRouteDto.failure(index, "CAR", "NO_ROUTE", "자동차 경로를 찾지 못했습니다.");
        if (route.summary() == null || !nonNegative(route.summary().duration()) || !nonNegative(route.summary().distance())) throw invalid();
        List<TravelRouteDto.Segment> segments = new ArrayList<>();
        // 도로 조각을 별도 선으로 그립니다. 누락 구간을 임의의 직선으로 연결하지 않습니다.
        if (route.sections() != null) for (var section : route.sections()) {
            if (section.roads() == null) continue;
            for (var road : section.roads()) {
                var vertices = road.vertexes();
                if (vertices == null || vertices.size() < 4) continue;
                if (vertices.size() % 2 != 0) throw invalid();
                List<LocationDto> points = new ArrayList<>();
                for (int i = 0; i < vertices.size(); i += 2) points.add(point(vertices.get(i), vertices.get(i + 1)));
                segments.add(new TravelRouteDto.Segment("CAR", points));
            }
        }
        return new TravelRouteDto(index, "CAR", "OK", geometryMessage(segments), route.summary().duration(),
                route.summary().distance(), null, segments);
    }

    private TravelRouteDto transit(int index, LocationDto origin, LocationDto destination) {
        var response = transitClient.get().uri(builder -> builder.path("/v2/routing/publictraffic")
                .queryParam("start_x", origin.longitude()).queryParam("start_y", origin.latitude())
                .queryParam("end_x", destination.longitude()).queryParam("end_y", destination.latitude()).build())
                .header("Authorization", "KakaoAK " + apiKey).retrieve().body(KakaoTransitRouteResponse.class);
        if (response == null || response.status() == null) throw invalid();
        if (List.of("NO_RESULTS", "STARTNODES_NULL", "ENDNODES_NULL").contains(response.status()))
            return TravelRouteDto.failure(index, "TRANSIT", "NO_ROUTE", "이 구간의 대중교통 경로가 없습니다. 도시 간 교통 지원 범위도 확인해주세요.");
        if (!response.status().equals("OK") || response.routes() == null) throw invalid();
        // 첫 버전은 API가 반환한 경로 중 가장 빠른 경로를 표시합니다.
        var route = response.routes().stream().filter(r -> r.properties() != null && nonNegative(r.properties().totalTime()))
                .min(Comparator.comparingInt(r -> r.properties().totalTime())).orElse(null);
        if (route == null) return TravelRouteDto.failure(index, "TRANSIT", "NO_ROUTE", "대중교통 경로를 찾지 못했습니다.");
        if (!nonNegative(route.properties().totalDistance()) || !nonNegative(route.properties().transfers())) throw invalid();
        List<TravelRouteDto.Segment> segments = new ArrayList<>();
        if (route.steps() != null) for (var step : route.steps()) {
            if (step.path() == null || step.path().points() == null) continue;
            List<LocationDto> points = new ArrayList<>();
            for (var coordinates : step.path().points()) {
                if (coordinates == null || coordinates.size() < 2) throw invalid();
                points.add(point(coordinates.get(0), coordinates.get(1)));
            }
            if (points.size() >= 2) segments.add(new TravelRouteDto.Segment(
                    step.properties() == null ? "TRANSIT" : step.properties().type(), points));
        }
        return new TravelRouteDto(index, "TRANSIT", "OK", geometryMessage(segments), route.properties().totalTime(),
                route.properties().totalDistance(), route.properties().transfers(), segments);
    }
    private boolean nonNegative(Integer value) { return value != null && value >= 0; }
    private LocationDto point(Double x, Double y) {
        if (x == null || y == null || !Double.isFinite(x) || !Double.isFinite(y) || Math.abs(x) > 180 || Math.abs(y) > 90) throw invalid();
        return new LocationDto(y, x);
    }
    private String geometryMessage(List<TravelRouteDto.Segment> segments) {
        return segments.isEmpty() ? "예상 시간은 조회됐지만 지도 경로 좌표가 없습니다." : "";
    }
    private KakaoApiException invalid() { return error("ROUTE_INVALID_RESPONSE", "경로 응답을 처리할 수 없습니다."); }
    private KakaoApiException error(String code, String message) { return new KakaoApiException(HttpStatus.BAD_GATEWAY, code, message); }
}
