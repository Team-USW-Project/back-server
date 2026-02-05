package com.uswproject.qna.dto;

import com.uswproject.qna.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter

public class PostListItemResponseDto {
    private Long postId;
    private String title;
    private Long memberId;
    private LocalDateTime createdAt;
    public PostListItemResponseDto(Post post) {

    this.postId = post.getId();
    this.title = post.getTitle();
    this.memberId = post.getMemberId();
    this.createdAt = post.getCreatedAt();
}
}
