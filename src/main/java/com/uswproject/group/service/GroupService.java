package com.uswproject.group.service;

import com.uswproject.group.dto.response.GroupCreateResponse;
import com.uswproject.group.dto.response.GroupDetailResponse;
import com.uswproject.group.dto.response.GroupJoinResponse;
import com.uswproject.group.dto.response.GroupListResponse;
import com.uswproject.group.entity.*;
import com.uswproject.group.repository.GroupInviteCodeRepository;
import com.uswproject.group.repository.GroupMemberRepository;
import com.uswproject.group.repository.GroupRepository;
import com.uswproject.member.entity.MemberEntity;
import com.uswproject.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    // 그룹 테이블 접근용
    private final GroupRepository groupRepository;

    // 그룹-회원 연결 테이블 접근용
    private final GroupMemberRepository groupMemberRepository;

    // 회원 테이블 접근용 (member 패키지)
    private final MemberRepository memberRepository;

    // 초대코드 테이블 접근용
    private final GroupInviteCodeRepository groupInviteCodeRepository;

    // 생성자 주입
    public GroupService(GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupInviteCodeRepository groupInviteCodeRepository,
                        MemberRepository memberRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupInviteCodeRepository = groupInviteCodeRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 1주차
     * 그룹 생성 API
     * 1. 생성자(member)가 존재하는지 확인
     * 2. 그룹 생성
     * 3. 생성자를 LEADER로 GroupMember에 저장
     */

    /**
     * 2주차 반영: 그룹 생성 시 inviteCode 발급
     * - group 생성
     * - creator를 LEADER로 group_members에 저장
     * - inviteCode 생성해서 group_invite_codes에 저장
     */
    @Transactional
    public GroupCreateResponse createGroup(String name, Long creatorId) {

        // 1) 생성자 멤버가 존재하는지 확인 (없으면 생성 불가)
        MemberEntity creator = memberRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("멤버 없음"));

        // 2) 그룹 생성 후 저장 (save해야 id가 생김)
        GroupEntity group = new GroupEntity(name);
        groupRepository.save(group);

        // 3) 생성자를 그룹 멤버로 자동 포함 (LEADER)
        GroupMemberEntity gm = new GroupMemberEntity(group, creator, GroupRole.LEADER);
        groupMemberRepository.save(gm);

        // 4) 초대코드 생성 (UUID 일부 사용 + 중복 방지)
        String inviteCode = generateUniqueInviteCode();

        // (선택) 만료 넣고 싶으면 expiredAt을 now+?? 로 설정
        // 만료 기능은 선택이라 기본은 null(만료 없음)로 둠
        LocalDateTime expiredAt = null;

        // 5) 초대코드 저장
        GroupInviteCodeEntity invite = new GroupInviteCodeEntity(group, inviteCode, expiredAt);
        groupInviteCodeRepository.save(invite);

        // 6) 응답: groupId + inviteCode + expiredAt
        return new GroupCreateResponse(group.getId(), inviteCode, expiredAt);
    }

    /**
     * 2주차
     * 그룹 가입 API 로직
     * - inviteCode로 그룹 찾기
     * - (선택) 만료/비활성 체크
     * - 중복 가입 방지
     * - group_members에 MEMBER로 저장
     * - 응답에 groupId, memberId, role 내려주기
     */
    @Transactional
    public GroupJoinResponse joinGroup(String inviteCode, Long memberId) {

        // 1) 초대코드로 초대코드 엔티티 조회
        GroupInviteCodeEntity invite = groupInviteCodeRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대코드"));

        // 2) (선택) 만료 체크
        // expiredAt이 null이면 만료 없음 처리 (entity의 isExpired())
        if (invite.isExpired()) {
            throw new IllegalArgumentException("만료된 초대코드");
        }

        // 3) (선택) 비활성 체크
        if (!invite.isActive()) {
            throw new IllegalArgumentException("비활성화된 초대코드");
        }

        // 4) 가입할 멤버 조회
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버 없음"));

        // 5) 어느 그룹인지 가져오기
        GroupEntity group = invite.getGroup();
        Long groupId = group.getId();

        // 6) 중복 가입 방지
        boolean alreadyJoined = groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberId);
        if (alreadyJoined) {
            throw new IllegalArgumentException("이미 가입된 멤버입니다.");
        }

        // 7) 중복 아니면 group_members에 저장 (기본 역할 MEMBER)
        GroupMemberEntity gm = new GroupMemberEntity(group, member, GroupRole.MEMBER);
        GroupMemberEntity saved = groupMemberRepository.save(gm);

        // 8) 성공 응답 (groupId + memberId + role)
        Long savedMemberId = saved.getMember().getId();

        return new GroupJoinResponse(
                saved.getGroup().getId(),
                //savedMemberId,
                saved.getMember().getId(),
                saved.getRole().name()
        );
    }

    /**
     * 1주차
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
     * 1주차
     * 그룹 목록 조회
     * id, name만 DTO로 변환해 반환
     */
    public List<GroupListResponse> getGroupList() {
        return groupRepository.findAll().stream()
                .map(g -> new GroupListResponse(
                        g.getId(),
                        g.getName()
                ))
                .toList();
    }

    /**
     * UUID 일부로 초대코드 생성 + 중복 방지
     */
    private String generateUniqueInviteCode() {
        while (true) {
            // 예: "a1b2c3d4..." 형태에서 앞 8자리만 사용
            String code = UUID.randomUUID().toString().replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();

            // 이미 존재하면 다시 뽑기
            if (!groupInviteCodeRepository.existsByInviteCode(code)) {
                return code;
            }
        }
    }
}