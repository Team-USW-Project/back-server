package com.uswProject.route.entity;

import com.uswProject.route.entity.enums.SegmentMode;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class RoutePlan {

    private static final int ZERO = 0;

    private final Place origin;
    private final Place destination;

    private final Duration totalduration;
    private final int totalDistanceMeters;
    private final int transferCount;

    private final List<Segment> segments;

    public RoutePlan(Place origin, Place destination, Duration totalduration, int totalDistanceMeters,
                     int transferCount, List<Segment> segments) {
        validatePlace(origin);
        validatePlace(destination);
        validateDuration(totalduration);
        validateDistance(totalDistanceMeters);
        validateCount(transferCount);
        validateSegment(segments);

        this.origin = origin;
        this.destination = destination;
        this.totalduration = totalduration;
        this.totalDistanceMeters = totalDistanceMeters;
        this.transferCount = transferCount;
        this.segments = Collections.unmodifiableList(segments);
    }

    public Segment lastTransitSegmentOrNull() {
        for (int i = segments.size() - 1; i >= 0; i--) {
            if (segments.get(i).isTransit()) {
                return segments.get(i);
            }
        }

        return null;
    }

    public int totalWalkingDistanceMeters() {
        int sum = 0;
        for (Segment s : segments) {
            if (s.getMode() == SegmentMode.WALK) {
                sum += s.getDistanceMeters();
            }
        }

        return sum;
    }

    private void validatePlace(Place place) {
        if (place == null) {
            throw new IllegalArgumentException("place는 null일 수 없습니다.");
        }
    }

    private void validateDuration(Duration duration) {
        if (duration == null || duration.isNegative()) {
            throw new IllegalArgumentException("duration은 음수가 아니어야 합니다.");
        }
    }

    private void validateDistance(int distance) {
        if (distance < ZERO) {
            throw new IllegalArgumentException("distance는 0보다 작으면 안됩니다.");
        }
    }

    private void validateCount(int count) {
        if (count < ZERO) {
            throw new IllegalArgumentException("transferCount는 0보다 작으면 안됩니다.");
        }
    }

    private void validateSegment(List<Segment> segments) {
        if (segments == null || segments.isEmpty()) {
            throw new IllegalArgumentException("segments는 필수로 필요합니다.");
        }
    }
}
