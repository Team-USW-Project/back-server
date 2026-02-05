package com.uswproject.qna.controller;

import com.uswproject.qna.dto.PostCreateRequestDto;
import com.uswproject.qna.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 임시: 로그인 붙기 전까지는 memberId를 헤더로 받자
    @PostMapping
    public ResponseEntity<Long> create(
            @RequestHeader("MEMBER-ID") Long memberId,
            @RequestBody @Valid PostCreateRequestDto request
    ) {
        Long postId = postService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }
}
