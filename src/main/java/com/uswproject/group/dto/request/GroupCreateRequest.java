package com.uswproject.group.dto.request;

public class GroupCreateRequest {

    private String name;
    private Long creatorMemberId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreatorMemberId() {
        return creatorMemberId;
    }

    public void setCreatorMemberId(Long creatorMemberId) {
        this.creatorMemberId = creatorMemberId;
    }

}
