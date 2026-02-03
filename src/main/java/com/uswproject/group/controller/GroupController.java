package com.uswproject.group.controller;

import com.uswproject.group.dto.request.GroupCreateRequest;
import com.uswproject.group.dto.response.GroupDetailResponse;
import com.uswproject.group.entity.GroupEntity;
import com.uswproject.group.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // 그룹 생성
    @PostMapping
    public Long createGroup(@RequestBody GroupCreateRequest request) {

        return groupService.createGroup(
                request.getName(),
                request.getCreatorMemberId()
        );
    }

    // 그룹 상세 조회
    @GetMapping("/{groupId}")
    public GroupDetailResponse getGroup(@PathVariable Long groupId) {
        return groupService.getGroupDetail(groupId);
    }

    // 그룹 목록 조회
    @GetMapping
    public List<GroupEntity> getGroups() {
        return groupService.getGroupList();
    }
}
