package com.uswproject.group.repository;

import com.uswproject.group.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository
        extends JpaRepository<GroupEntity, Long> {
}
