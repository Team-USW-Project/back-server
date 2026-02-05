package com.uswProject.route.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoutePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routePlan_id", nullable = false)
    private Long id;

    private String location;

    private LocalDateTime totalDurationMin;

    private double totalDistanceM;

    private int transferCount;

    private List<Segment> segmentList;

}
