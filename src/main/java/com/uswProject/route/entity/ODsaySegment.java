package com.uswProject.route.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ODsaySegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "segment_id", nullable = false)
    private Long id;

    private SegmentMode mode;

    private Place from;

    private Place to;

    private LocalDateTime durationMin;

    private double distanceM;

}
