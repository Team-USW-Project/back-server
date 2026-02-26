package com.uswproject.qna.repository;

import com.uswproject.qna.entity.PostReaction;
import com.uswproject.qna.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    Optional<PostReaction> findByMemberIdAndPost_Id(Long memberId, Long postId);

    long countByPost_IdAndType(Long postId, ReactionType type);

    interface PostReactionCountRow {
        Long getPostId();
        ReactionType getType();
        Long getCnt();
    }

    @Query("""
        select r.post.id as postId, r.type as type, count(r) as cnt
        from PostReaction r
        where r.post.id in :postIds
        group by r.post.id, r.type
    """)
    List<PostReactionCountRow> countByPostIdsGroupByType(@Param("postIds") List<Long> postIds);

    @Query("""
        select r.post.id as postId, r.type as type, count(r) as cnt
        from PostReaction r
        where r.post.id = :postId
        group by r.post.id, r.type
    """)
    List<PostReactionCountRow> countByPostIdGroupByType(@Param("postId") Long postId);
}



