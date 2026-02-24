package com.uswproject.qna.controller;

import com.uswproject.qna.dto.CommentCreateRequestDto;
import com.uswproject.qna.dto.CommentListItemResponseDto;
import com.uswproject.qna.dto.CommentUpdateRequestDto;
import com.uswproject.qna.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<Long> create(
            @RequestHeader("MEMBER-ID") Long memberId,
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreateRequestDto request
    ) {
        Long commentId = commentService.create(memberId, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
    }

    // 댓글 목록
    @GetMapping
    public ResponseEntity<List<CommentListItemResponseDto>> getComments(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> update(
            @RequestHeader("MEMBER-ID") Long memberId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequestDto request
    ) {
        commentService.update(memberId, postId, commentId, request);
        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("MEMBER-ID") Long memberId,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.delete(memberId, postId, commentId);
        return ResponseEntity.noContent().build();
    }
}