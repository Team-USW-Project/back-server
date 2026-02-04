package com.uswProject.userLastTime.repository;

import com.uswProject.userLastTime.entity.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLastTimeRepository extends JpaRepository<UserRoute, Long> {
}
