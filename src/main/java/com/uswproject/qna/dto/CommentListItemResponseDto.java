package com.uswproject.qna.dto;

import com.uswproject.qna.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class CommentListItemResponseDto {

    private Long id;
    private Long memberId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentListItemResponseDto(Comment comment) {
        this.id = comment.getId();
        this.memberId = comment.getMemberId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }

}