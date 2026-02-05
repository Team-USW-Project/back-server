package com.uswproject.qna.controller;

import com.uswproject.qna.dto.PostCreateRequestDto;
import com.uswproject.qna.dto.PostUpdateRequestDto;
import com.uswproject.qna.service.PostService;
import com.uswproject.qna.service.PostUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    // 게시글 작성
    @PostMapping
    public ResponseEntity<Long> create(
            @RequestHeader("MEMBER-ID") Long memberId,
            @RequestBody @Valid PostCreateRequestDto request
    ) {
        Long postId = postService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    //게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("MEMBER-ID") Long memberId,
            @PathVariable Long postId
    ) {
        postService.delete(memberId, postId);
        return ResponseEntity.noContent().build(); // 204
    }

    //게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<Void> update(
            @RequestHeader("MEMBER-ID") Long memberId,
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequestDto request
    ) {
        postService.update(memberId, postId, request);
        return ResponseEntity.noContent().build();
    }

}
