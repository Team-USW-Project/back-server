package com.uswproject.group.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.uswproject.member.entity.MemberEntity;

@Entity
@Table(name = "group_members")
public class GroupMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 중간테이블 PK

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;
    // 소속 그룹

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;
    // 멤버

    @Enumerated(EnumType.STRING)
    private GroupRole role;
    // LEADER / MEMBER

    private LocalDateTime joinedAt;
    // 그룹 참여 시각

    protected GroupMemberEntity() {}

    public GroupMemberEntity(GroupEntity group, MemberEntity member, GroupRole role) {
        this.group = group;
        this.member = member;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }

    public MemberEntity getMember() { return member; }
    public GroupRole getRole() { return role; }
}
