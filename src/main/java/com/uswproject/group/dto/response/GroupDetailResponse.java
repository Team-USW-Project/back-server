package com.uswproject.group.dto.response;

import java.util.List;

public class GroupDetailResponse {

    public Long groupId;
    public String name;
    public List<MemberInfo> members;

    public GroupDetailResponse(Long groupId, String name, List<MemberInfo> members) {
        this.groupId = groupId;
        this.name = name;
        this.members = members;
    }

    public static class MemberInfo {
        public String name;
        public String role;

        public MemberInfo(String name, String role) {
            this.name = name;
            this.role = role;
        }
    }
}
