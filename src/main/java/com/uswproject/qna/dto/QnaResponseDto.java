package com.uswproject.qna.dto;


import com.uswproject.qna.entity.Qna;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * QnaResponseDto
 * - 클라이언트에게 게시글 조회 결과를 전달하는 DTO
 * - Entity 전체를 노출하지 않고 필요한 정보만 추려 전달
 */

@Getter

public class QnaResponseDto {
    private  Long id;                   // 게시글 ID
    private String title;               // 제목
    private String content;             // 내용
    private String writer;              // 작성자
    private LocalDateTime createdAt;    // 생성일
    private LocalDateTime updatedAt;    // 수정일

    /**
     * Entity → ResponseDto 변환 생성자
     * @param entity DB에서 조회한 Qna 엔티티
     */

    public QnaResponseDto(Qna entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.writer = entity.getWriter();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}
