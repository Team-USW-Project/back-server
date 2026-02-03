package com.uswproject.group.repository;

import com.uswproject.group.entity.GroupMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository
        extends JpaRepository<GroupMemberEntity, Long> {

    List<GroupMemberEntity> findByGroupId(Long groupId);
    // findByGroupId : 이 그룹에 속한 멤버 전부 가져오기
}
