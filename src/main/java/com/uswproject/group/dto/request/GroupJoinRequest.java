package com.uswproject.group.dto.request;

/**
 * GroupJoinRequest (그룹 가입 요청 DTO)
 *
 * Postman 예시:
 * {
 *   "inviteCode": "ABCDEF12",
 *   "memberId": 1
 * }
 */
public class GroupJoinRequest {

    private String inviteCode;
    private Long memberId;

    public String getInviteCode() { return inviteCode; }
    public Long getMemberId() { return memberId; }
}