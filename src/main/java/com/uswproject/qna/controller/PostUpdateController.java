package com.uswproject.qna.controller;

import com.uswproject.qna.dto.PostUpdateRequestDto;
import com.uswproject.qna.service.PostUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostUpdateController {

    private final PostUpdateService postUpdateService;

    @PatchMapping("/{postId}")
    public ResponseEntity<Void> update(
            @RequestHeader("MEMBER-ID") Long memberId,
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequestDto request
    ) {
        postUpdateService.update(memberId, postId, request);
        return ResponseEntity.noContent().build();
    }
}
