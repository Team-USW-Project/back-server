package com.uswProject.route.entity;

import com.uswProject.route.entity.enums.PlaceType;
import lombok.Getter;

@Getter
public class Place {

    private static final double MIN_LAT = -90.0;
    private static final double MAX_LAT = 90.0;
    private static final double MIN_LNG = -180.0;
    private static final double MAX_LNG = 180.0;

    private final String name;
    private final PlaceType type;
    private final double lat;
    private final double lng;


    public Place(String name, PlaceType type, double lat, double lng) {
        validateType(type);
        validateLocation(lat, lng);

        this.name = name.trim();
        this.type = type;
        this.lat = lat;
        this.lng = lng;
    }

    private static void validateType(PlaceType type) {
        if (type == null) {
            throw new IllegalArgumentException("장소타입은 필수값입니다.");
        }
    }

    private static void validateLocation(double lat, double lng) {
        if (lat < MIN_LAT || lat > MAX_LAT) {
            throw new IllegalArgumentException("위도는 -90도에서 90도 사이어야 합니다.");
        }

        if (lng < MIN_LNG || lng > MAX_LNG) {
            throw new IllegalArgumentException("경도는 -180도~180도 사이어야 합니다.");
        }
    }
}
