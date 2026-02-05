package com.uswproject.qna.repository;

import com.uswproject.qna.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
