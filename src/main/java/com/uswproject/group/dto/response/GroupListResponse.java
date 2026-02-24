package com.uswproject.group.dto.response;


public class GroupListResponse {
    private Long groupId;     // 그룹 PK
    private String groupName; // 그룹 이름

    public GroupListResponse() {}

    public GroupListResponse(Long groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }
}
