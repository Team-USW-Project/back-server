package com.uswProject.userLastTime.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 세부경로를 저장하기 위한 Entity
 */
@Entity
@Table(name = "segmentRoutes")
@Getter
@NoArgsConstructor
public class SegmentRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "segment_route_id", nullable = false)
    private Long id;
}
