package com.uswProject.route.entity;

import com.uswProject.route.entity.enums.SegmentMode;
import java.time.Duration;
import lombok.Getter;

@Getter
public class Segment {

    private final SegmentMode mode;
    private final Place from;
    private final Place to;

    private final Duration duration;
    private final int distanceMeters;

    private final TransitLine line;

    public Segment(SegmentMode mode, Place from, Place to, Duration duration, int distanceMeters, TransitLine line) {
        this.mode = mode;
        this.from = from;
        this.to = to;
        this.duration = duration;
        this.distanceMeters = distanceMeters;
        this.line = line;
    }
    public boolean isTransit() {
        return mode == SegmentMode.BUS || mode == SegmentMode.SUBWAY;
    }

    public boolean isSubway() {
        return mode == SegmentMode.SUBWAY;
    }
}
