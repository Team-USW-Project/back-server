package com.uswproject.group.dto.response;

/**
 * inviteCode로 그룹 가입 성공 시 응답 DTO
 * - 어떤 그룹에 가입했는지 (groupId)
 * - 어떤 멤버가 가입했는지 (memberId)
 * - 가입된 역할이 무엇인지 (role)
 */
public class GroupJoinResponse {

    private final Long groupId;
    private final Long memberId;
    private final String role;

    public GroupJoinResponse(Long groupId, Long memberId, String role) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.role = role;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getRole() {
        return role;
    }
}