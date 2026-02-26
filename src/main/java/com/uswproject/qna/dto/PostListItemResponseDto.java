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

    private long likeCount;
    private long dislikeCount;

    public PostListItemResponseDto(Post post, long likeCount, long dislikeCount) {

    this.postId = post.getId();
    this.title = post.getTitle();
    this.memberId = post.getMemberId();
    this.createdAt = post.getCreatedAt();
    this.likeCount = likeCount;
    this.dislikeCount = dislikeCount;
}

    public PostListItemResponseDto(Post post){
        this(post, 0 , 0);
    }
}
