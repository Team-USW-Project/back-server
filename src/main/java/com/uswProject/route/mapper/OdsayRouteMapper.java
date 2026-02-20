package com.uswProject.route.mapper;

import com.uswProject.route.dto.odsay.OdsayLane;
import com.uswProject.route.dto.odsay.OdsayPath;
import com.uswProject.route.dto.odsay.OdsaySearchResponse;
import com.uswProject.route.dto.odsay.OdsaySubPath;
import com.uswProject.route.entity.Place;
import com.uswProject.route.entity.RoutePlan;
import com.uswProject.route.entity.Segment;
import com.uswProject.route.entity.TransitLine;
import com.uswProject.route.entity.enums.PlaceType;
import com.uswProject.route.entity.enums.SegmentMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OdsayRouteMapper {

    private static final Map<Integer, SegmentMode> TRAFFIC_TYPE_TO_MODE = Map.of(1, SegmentMode.SUBWAY, 2, SegmentMode.BUS, 3, SegmentMode.WALK);
    private static final EnumMap<SegmentMode, PlaceType> MODE_TO_PLACE_TYPE = new EnumMap<>(SegmentMode.class);

    static {
        MODE_TO_PLACE_TYPE.put(SegmentMode.SUBWAY, PlaceType.STATION);
        MODE_TO_PLACE_TYPE.put(SegmentMode.BUS, PlaceType.BUS_STOP);
        MODE_TO_PLACE_TYPE.put(SegmentMode.WALK, PlaceType.POINT);
    }

    /**
     * 한 경로에 대한 RoutePlan들을 List형태로 만들기
     * @param response
     * @param origin
     * @param destination
     * @return origin부터 destination까지의 여러 경로 리스트
     */
    public List<RoutePlan> toRoutePlans(OdsaySearchResponse response, Place origin, Place destination) {
        if (response == null || response.getResult() == null || response.getResult().getPath() == null) {
            return List.of();
        }

        List<RoutePlan> plans = new ArrayList<>();

        for (OdsayPath path : response.getResult().getPath()) {
            if (path == null || path.getInfo() == null || path.getSubPath() == null || path.getSubPath().isEmpty()) {
                continue;
            }
            plans.add(toRoutePlan(path, origin, destination));
        }
        return plans;
    }

    private RoutePlan toRoutePlan(OdsayPath path, Place origin, Place destination) {
        List<Segment> segments = new ArrayList<>();
        for (OdsaySubPath subPath : path.getSubPath()) {
            if (subPath == null) continue;
            segments.add(toSegment(subPath));
        }

        int transferCount = Math.max(0, path.getInfo().getBusTransitCount() + path.getInfo().getSubwayTransitCount());

        return new RoutePlan(
                origin,
                destination,
                Duration.ofMinutes(path.getInfo().getTotalTime()),
                (int) Math.round(path.getInfo().getTotalDistance()),
                transferCount,
                segments
        );
    }

    private Segment toSegment(OdsaySubPath subPath) {
        SegmentMode mode = toMode(subPath.getTrafficType());
        PlaceType placeType = guessPlaceType(mode);

        Place from = toPlaceFromStart(subPath, placeType);
        Place to = toPlaceFromEnd(subPath, placeType);

        TransitLine line = toTransitLine(mode, subPath);

        return new Segment(
                mode,
                from,
                to,
                Duration.ofMinutes(subPath.getSectionTime()),
                (int) Math.round(subPath.getDistance()),
                line
        );
    }

    private Place toPlaceFromStart(OdsaySubPath subPath, PlaceType type) {
        return new Place(subPath.getStartName(), type, subPath.getStartX(), subPath.getStartY());
    }

    private Place toPlaceFromEnd(OdsaySubPath subPath, PlaceType type) {
        return new Place(subPath.getEndName(), type, subPath.getEndX(), subPath.getEndY());
    }

    private SegmentMode toMode(int trafficType) {
        return TRAFFIC_TYPE_TO_MODE.getOrDefault(trafficType, SegmentMode.WALK);
    }

    private PlaceType guessPlaceType(SegmentMode mode) {
        return MODE_TO_PLACE_TYPE.get(mode);
    }

    private TransitLine toTransitLine(SegmentMode mode, OdsaySubPath subPath) {
        if (mode == SegmentMode.WALK) {
            return null;
        }

        OdsayLane lane = findLane(subPath);

        if (lane == null) {
            return new TransitLine(null, subPath.getWay(), null);
        }
        String name = findName(mode, lane);
        String providerId = findProviderId(mode, lane);

        return new TransitLine(name, subPath.getWay(), providerId);
    }

    private OdsayLane findLane(OdsaySubPath subPath) {
        if (subPath.getLane() == null || subPath.getLane().isEmpty()) {
            return null;
        }

        return subPath.getLane().get(0);
    }

    private String findName(SegmentMode mode, OdsayLane lane) {
        if (mode == SegmentMode.SUBWAY) {
            return lane.getName();
        }

        return lane.getBusNo();
    }

    private String findProviderId(SegmentMode mode, OdsayLane lane) {
        if (mode == SegmentMode.SUBWAY && lane.getSubwayCode() != null) {
            return String.valueOf(lane.getSubwayCode());
        }

        if (mode == SegmentMode.BUS && lane.getBusID() != null) {
            return String.valueOf(lane.getBusID());
        }

        return null;
    }
}
