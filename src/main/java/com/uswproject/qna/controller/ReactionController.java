package com.uswproject.qna.controller;

import com.uswproject.qna.entity.ReactionType;
import com.uswproject.qna.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reactions")
public class ReactionController {

    private final ReactionService reactionService;

    // 게시글 좋아요/싫어요
    @PostMapping("/posts/{postId}")
    public ResponseEntity<Void> reactPost(
            @RequestHeader("MEMBER-ID") Long memberId,
            @PathVariable Long postId,
            @RequestParam ReactionType type
    ) {
        reactionService.reactToPost(memberId, postId, type);
        return ResponseEntity.noContent().build();
    }

    // 댓글 좋아요/싫어요
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Void> reactComment(
            @RequestHeader("MEMBER-ID") Long memberId,
            @PathVariable Long commentId,
            @RequestParam ReactionType type
    ) {
        reactionService.reactToComment(memberId, commentId, type);
        return ResponseEntity.noContent().build();
    }
}