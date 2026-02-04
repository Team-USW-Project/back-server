package com.uswProject.userLastTime.entity;

import com.uswProject.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 출발지, 도착지와 함께 세부경로를 저장하기 위한 Entity
 * 하나의 경로에 여러가지의 세부경로가 존재하기에 SegmentRoute와 1:N 연관관계를 통해 List형태로 가지고 있을 예정
 */
@Entity
@Table(name = "userRoutes")
@Getter
@NoArgsConstructor
public class UserRoute extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userRoute_id", nullable = false)
    private Long id;

}
