package com.uswproject.qna.service;

import com.uswproject.qna.dto.CommentCreateRequestDto;
import com.uswproject.qna.dto.CommentListItemResponseDto;
import com.uswproject.qna.dto.CommentUpdateRequestDto;
import com.uswproject.qna.entity.Comment;
import com.uswproject.qna.entity.Post;
import com.uswproject.qna.repository.CommentRepository;
import com.uswproject.qna.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 댓글 작성
    @Transactional
    public Long create(Long memberId, Long postId, CommentCreateRequestDto request) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));

        Comment comment = new Comment(post, memberId, request.getContent());
        return commentRepository.save(comment).getId();
    }

    // 댓글 목록
    @Transactional(readOnly = true)
    public List<CommentListItemResponseDto> getComments(Long postId) {
        // 게시글 존재 확인
        postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));

        return commentRepository.findAllByPostIdAndDeletedFalseOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentListItemResponseDto::new)
                .toList();
    }

    // 댓글 수정
    @Transactional
    public void update(Long memberId, Long postId, Long commentId, CommentUpdateRequestDto request) {
        // 게시글 존재 확인
        postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));

        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다. id=" + commentId));

        // 다른 게시글 댓글을 수정하려는 경우 방지
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다. commentId=" + commentId);
        }

        if (!comment.getMemberId().equals(memberId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        comment.update(request.getContent());
    }

    // 댓글 삭제
    @Transactional
    public void delete(Long memberId, Long postId, Long commentId) {
        postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));

        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다. id=" + commentId));

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다. commentId=" + commentId);
        }

        if (!comment.getMemberId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        comment.softDelete();
    }
}