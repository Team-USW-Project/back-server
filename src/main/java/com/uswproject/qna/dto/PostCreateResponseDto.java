package com.uswproject.qna.dto;

import java.util.List;

public record PostCreateResponseDto(
        Long postId,
        List<String> imageUrls
) {}
