package com.uswproject.group.dto.response;

import java.time.LocalDateTime;

/**
 * GroupCreateResponse (그룹 생성 응답 DTO)
 * groupId + inviteCode를 같이 반환하도록 개선
 */
public class GroupCreateResponse {

    private Long groupId;
    private String inviteCode;
    private LocalDateTime expiredAt; // (선택) 만료시간 보여주고 싶으면

    public GroupCreateResponse(Long groupId, String inviteCode, LocalDateTime expiredAt) {
        this.groupId = groupId;
        this.inviteCode = inviteCode;
        this.expiredAt = expiredAt;
    }

    public Long getGroupId() { return groupId; }
    public String getInviteCode() { return inviteCode; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
}