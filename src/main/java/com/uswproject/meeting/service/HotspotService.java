package com.uswproject.meeting.service;

import com.uswproject.kakao.client.KakaoLocalClient;
import com.uswproject.kakao.exception.KakaoApiException;
import com.uswproject.meeting.dto.*;
import com.uswproject.meeting.dto.HotspotResponseDto.Area;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotspotService {
    private final MidpointService midpointService;
    private final KakaoLocalClient kakaoLocalClient;

    // 시연용으로 선정한 상권의 대략적인 기준 좌표입니다. 역 출입구·도보 경로 좌표가 아닙니다.
    private record Spot(String id, String name, String description, double latitude, double longitude) {}
    private static final List<Spot> SPOTS = List.of(
        new Spot("hongdae", "홍대", "홍대입구역 주변 상권", 37.5572, 126.9245),
        new Spot("sinchon", "신촌", "신촌역 주변 상권", 37.5551, 126.9369),
        new Spot("gangnam", "강남", "강남역 주변 상권", 37.4979, 127.0276),
        new Spot("konkuk", "건대", "건대입구역 주변 상권", 37.5404, 127.0692),
        new Spot("jongno", "종로", "종각역 주변 상권", 37.5702, 126.9831),
        new Spot("sadang", "사당", "사당역 주변 상권", 37.4765, 126.9816),
        new Spot("jamsil", "잠실", "잠실역 주변 상권", 37.5133, 127.1001),
        new Spot("yeongdeungpo", "영등포", "영등포역 주변 상권", 37.5157, 126.9076),
        new Spot("suwon", "수원역", "수원역 주변 상권", 37.2657, 126.9998),
        new Spot("ingye", "수원 인계동", "수원시청역 주변 상권", 37.2619, 127.0307),
        new Spot("beomgye", "범계", "범계역 주변 상권", 37.3898, 126.9508),
        new Spot("anyang", "안양", "안양역 주변 상권", 37.4016, 126.9229),
        new Spot("seohyeon", "분당 서현", "서현역 주변 상권", 37.3851, 127.1235),
        new Spot("pangyo", "판교", "판교역 주변 상권", 37.3948, 127.1112),
        new Spot("bucheon", "부천", "부천역 주변 상권", 37.4841, 126.7828),
        new Spot("ilsan", "일산 정발산", "정발산역 주변 상권", 37.6595, 126.7734),
        new Spot("uijeongbu", "의정부", "의정부역 주변 상권", 37.7384, 127.0458),
        new Spot("yeongtong", "영통", "영통역 주변 상권", 37.2516, 127.0713)
    );

    public HotspotResponseDto recommend(HotspotRequestDto request) {
        // 수도권 후보만 있는 상태에서 먼 지역 사용자에게 서울을 억지로 추천하지 않습니다.
        for (LocationDto location : request.locations()) {
            double nearest = SPOTS.stream().mapToDouble(spot -> distance(location,
                    new LocationDto(spot.latitude(), spot.longitude()))).min().orElseThrow();
            if (nearest > 80000) throw new KakaoApiException(HttpStatus.BAD_REQUEST,
                    "AREA_NOT_SUPPORTED", "현재 서울·경기 주요 역세권을 지원합니다. 수도권 출발지를 선택해주세요.");
        }
        List<Area> ranked = SPOTS.stream().map(spot -> score(spot, request.locations()))
                .sorted(Comparator.comparingDouble(area -> area.maxDistanceMeters() + area.averageDistanceMeters() * 0.25))
                .limit(3).toList();
        Area selected = ranked.get(0);
        if (request.areaId() != null) {
            selected = ranked.stream().filter(area -> area.id().equals(request.areaId())).findFirst()
                    .orElseThrow(() -> new KakaoApiException(HttpStatus.BAD_REQUEST, "INVALID_AREA",
                            "추천 목록에서 번화가를 선택해주세요."));
        }
        String category = request.category() == null ? "CAFE" : request.category();
        var places = kakaoLocalClient.searchPlaces(selected.location(), 500,
                category.equals("RESTAURANT") ? "FD6" : "CE7");
        return new HotspotResponseDto(midpointService.calculate(request.locations()), selected, ranked, category, 500, places);
    }

    private Area score(Spot spot, List<LocationDto> locations) {
        LocationDto center = new LocationDto(spot.latitude(), spot.longitude());
        double sum = 0, max = 0;
        for (LocationDto location : locations) {
            double meters = distance(location, center);
            sum += meters;
            max = Math.max(max, meters);
        }
        return new Area(spot.id(), spot.name(), spot.description(), center,
                Math.round(sum / locations.size()), Math.round(max));
    }

    // Haversine 공식: 위도·경도 두 점 사이의 지표면 직선거리를 미터로 계산합니다.
    private double distance(LocationDto a, LocationDto b) {
        double lat = Math.toRadians(b.latitude() - a.latitude());
        double lng = Math.toRadians(b.longitude() - a.longitude());
        double h = Math.pow(Math.sin(lat / 2), 2) + Math.cos(Math.toRadians(a.latitude()))
                * Math.cos(Math.toRadians(b.latitude())) * Math.pow(Math.sin(lng / 2), 2);
        return 6371000 * 2 * Math.asin(Math.sqrt(Math.min(1, h)));
    }
}
