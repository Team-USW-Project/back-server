package com.uswproject.qna.repository;

import com.uswproject.qna.entity.CommentReaction;
import com.uswproject.qna.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {
    Optional<CommentReaction> findByMemberIdAndCommentId(Long memberId, Long commentId);
    long countByCommentIdAndType(Long commentId, ReactionType type);
}