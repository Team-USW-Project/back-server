package com.uswproject.qna.dto;

import java.time.LocalDateTime;



public record PostDetailResponseDto(
        Long postId,
        String title,
        String content,
        Long memberId,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        long likeCount,
        long dislikeCount
) {
}
