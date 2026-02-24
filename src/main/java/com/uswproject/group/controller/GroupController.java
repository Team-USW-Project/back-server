package com.uswproject.group.controller;

import com.uswproject.group.dto.request.GroupCreateRequest;
import com.uswproject.group.dto.request.GroupJoinRequest;
import com.uswproject.group.dto.response.GroupCreateResponse;
import com.uswproject.group.dto.response.GroupDetailResponse;
import com.uswproject.group.dto.response.GroupJoinResponse;
import com.uswproject.group.dto.response.GroupListResponse;
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
    // 2주차: 생성 시 inviteCode 발급해서 같이 반환
    @PostMapping
    public GroupCreateResponse createGroup(@RequestBody GroupCreateRequest request) {

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
    public List<GroupListResponse> getGroups() {
        return groupService.getGroupList();
    }

    /**
     * 그룹 가입 API
     * POST /groups/join
     */
    @PostMapping("/join")
    public GroupJoinResponse joinGroup(@RequestBody GroupJoinRequest request) {
        return groupService.joinGroup(
                request.getInviteCode(),
                request.getMemberId()
        );
    }
}
