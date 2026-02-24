package com.uswproject.group.repository;

import com.uswproject.group.entity.GroupInviteCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * GroupInviteCodeRepository (그룹 초대코드 레포지토리)
 * - inviteCode로 조회/존재여부 확인이 핵심
 */
public interface GroupInviteCodeRepository extends JpaRepository<GroupInviteCodeEntity, Long> {

    Optional<GroupInviteCodeEntity> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);
}