package com.uswproject.qna.controller;

import com.uswproject.qna.service.PostDeleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts") // application.yml context-path가 /api/v1 이면 최종은 /api/v1/posts
@RequiredArgsConstructor
public class PostDeleteController {

    private final PostDeleteService postDeleteService;

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("MEMBER-ID") Long memberId,
            @PathVariable Long postId
    ) {
        postDeleteService.delete(memberId, postId);
        return ResponseEntity.noContent().build(); // 204
    }
}
