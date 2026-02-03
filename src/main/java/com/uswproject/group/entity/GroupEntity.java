package com.uswproject.group.entity;

import com.uswproject.member.entity.MemberEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

//@Entity
//@Table(name = "groups")
//public class GroupEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    // 그룹 PK
//
//    @Column(nullable = false)
//    private String name;
//    // 그룹 이름
//
//    @Enumerated(EnumType.STRING)
//    private GroupStatus status;
//    // 지금은 CREATED만 씀
//
//    private LocalDateTime createdAt;
//    // 그룹 생성 시간
//
//    protected GroupEntity() {}
//    // JPA가 객체 만들 때 필요
//
//    public GroupEntity(String name) {
//        this.name = name;
//        this.status = GroupStatus.CREATED;
//        this.createdAt = LocalDateTime.now();
//    }
//
//    // getter
//    public Long getId() { return id; }
//    public String getName() { return name; }
//    public GroupStatus getStatus() { return status; }

@Entity
@Table(name = "groups")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 그룹 PK

    @Column(nullable = false)
    private String name;
    // 그룹 이름

    protected GroupEntity() {
        // JPA 기본 생성자 (필수)
    }

    public GroupEntity(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
