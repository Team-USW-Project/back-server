package com.uswProject.route.entity;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class RoutePlan {

    private final Place origin;
    private final Place destination;

    private final Duration totalduration;
    private final int totalDistanceMeters;
    private final int transferCount;

    private final List<Segment> segments;

    public RoutePlan(Place origin, Place destination, Duration totalduration, int totalDistanceMeters,
                     int transferCount, List<Segment> segments) {
        this.origin = origin;
        this.destination = destination;
        this.totalduration = totalduration;
        this.totalDistanceMeters = totalDistanceMeters;
        this.transferCount = transferCount;
        this.segments = Collections.unmodifiableList(segments);
    }

}
