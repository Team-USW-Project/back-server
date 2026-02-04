package com.uswproject.qna.controller;

import com.uswproject.qna.dto.PostCreateRequestDto;
import com.uswproject.qna.dto.PostCreateResponseDto;
import com.uswproject.qna.service.PostService;
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

    @PostMapping
    public ResponseEntity<PostCreateResponseDto> create(
            @RequestHeader("MEMBER-ID") Long memberId,
            @RequestBody @Valid PostCreateRequestDto request
    ) {
        PostCreateResponseDto response = postService.create(memberId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
