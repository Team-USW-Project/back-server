package com.uswproject.group.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * GroupInviteCodeEntity (그룹 초대코드 엔티티)
 * 핵심 요구사항:
 * - inviteCode 하나로 여러 명 가입 가능
 * - (선택) 만료(expiredAt) 넣을 수 있게 설계
 */

@Entity
@Table(
        name = "group_invite_codes",
        uniqueConstraints = @UniqueConstraint(name = "uk_invite_code", columnNames = "invite_code")
)
public class GroupInviteCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group; // 어떤 그룹의 초대코드인지

    @Column(name = "invite_code", nullable = false, unique = true, length = 32)
    private String inviteCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성 시각

    @Column(name = "expired_at")
    private LocalDateTime expiredAt; // 만료 시각 (선택: null이면 만료 없음)

    @Column(name = "is_active", nullable = false)
    private boolean isActive; // 활성/비활성

    protected GroupInviteCodeEntity() {}

    public GroupInviteCodeEntity(GroupEntity group, String inviteCode, LocalDateTime expiredAt) {
        this.group = group;
        this.inviteCode = inviteCode;
        this.createdAt = LocalDateTime.now();
        this.expiredAt = expiredAt;
        this.isActive = true;
    }

    /** 만료 체크: expiredAt이 null이면 만료 없음으로 처리 */
    public boolean isExpired() {
        if (expiredAt == null) return false;
        return expiredAt.isBefore(LocalDateTime.now());
    }

    public Long getId() { return id; }
    public GroupEntity getGroup() { return group; }
    public String getInviteCode() { return inviteCode; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public boolean isActive() { return isActive; }
}
