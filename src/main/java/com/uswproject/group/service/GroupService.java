package com.uswproject.group.service;

import com.uswproject.group.dto.response.GroupDetailResponse;
import com.uswproject.group.entity.*;
import com.uswproject.group.repository.GroupMemberRepository;
import com.uswproject.group.repository.GroupRepository;
import com.uswproject.member.entity.MemberEntity;
import com.uswproject.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    // 그룹 테이블 접근용
    private final GroupRepository groupRepository;

    // 그룹-회원 연결 테이블 접근용
    private final GroupMemberRepository groupMemberRepository;

    // 회원 테이블 접근용 (member 패키지)
    private final MemberRepository memberRepository;

    // 생성자 주입
    public GroupService(GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        MemberRepository memberRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 그룹 생성 API
     * 1. 생성자(member)가 존재하는지 확인
     * 2. 그룹 생성
     * 3. 생성자를 LEADER로 GroupMember에 저장
     */
    @Transactional
    public Long createGroup(String name, Long creatorId) {

        // 그룹 생성자 조회
        MemberEntity creator = memberRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("멤버 없음"));

        // 그룹 생성
        GroupEntity group = new GroupEntity(name);
        groupRepository.save(group);

        // 생성자를 LEADER로 그룹에 포함
        GroupMemberEntity gm =
                new GroupMemberEntity(group, creator, GroupRole.LEADER);

        groupMemberRepository.save(gm);

        return group.getId();
    }

    /**
     * 그룹 상세 조회
     * - 그룹 정보
     * - 그룹에 속한 멤버 목록 + 역할
     */
    public GroupDetailResponse getGroupDetail(Long groupId) {

        // 그룹 조회
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 없음"));

        // 해당 그룹에 속한 멤버들 조회
        List<GroupMemberEntity> members =
                groupMemberRepository.findByGroupId(groupId);

        // 응답 DTO로 변환
        List<GroupDetailResponse.MemberInfo> memberInfos =
                members.stream()
                        .map(gm -> new GroupDetailResponse.MemberInfo(
                                gm.getMember().getName(),
                                gm.getRole().name()
                        ))
                        .toList();

        return new GroupDetailResponse(
                group.getId(),
                group.getName(),
                memberInfos
        );
    }

    /**
     * 그룹 목록 조회
     */
    public List<GroupEntity> getGroupList() {
        return groupRepository.findAll();
    }
}
