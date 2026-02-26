package com.uswproject.qna.service;

import com.uswproject.qna.entity.*;
import com.uswproject.qna.repository.CommentReactionRepository;
import com.uswproject.qna.repository.CommentRepository;
import com.uswproject.qna.repository.PostReactionRepository;
import com.uswproject.qna.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final PostRepository postRepository;
    private final PostReactionRepository postReactionRepository;
    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;

    @Transactional
    public void reactToPost(Long memberId, Long postId, ReactionType type) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));

        postReactionRepository.findByMemberIdAndPost_Id(memberId, postId)
                .ifPresentOrElse(existing -> {
                    if (existing.getType() == type) {
                        postReactionRepository.delete(existing); // 같은 버튼 다시 누르면 취소
                    } else {
                        existing.changeType(type); // 좋아요 <-> 싫어요 전환
                    }
                }, () -> postReactionRepository.save(new PostReaction(memberId, post, type)));
    }

    @Transactional(readOnly = true)
    public long countPostReaction(Long postId, ReactionType type) {
        return postReactionRepository.countByPost_IdAndType(postId, type);
    }
    @Transactional
    public void reactToComment(Long memberId, Long commentId, ReactionType type) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다. id=" + commentId));

        commentReactionRepository.findByMemberIdAndCommentId(memberId, commentId)
                .ifPresentOrElse(existing -> {

                    // 같은 버튼 다시 누르면 취소
                    if (existing.getType() == type) {
                        commentReactionRepository.delete(existing);
                    } else {
                        // 좋아요 <-> 싫어요 전환
                        existing.changeType(type);
                    }

                }, () -> {
                    // 처음 누르는 경우 생성
                    commentReactionRepository.save(
                            new CommentReaction(memberId, comment, type)
                    );
                });
    }
}