package com.uswproject.qna.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * QnaRequestDto
 * - 사용자가 게시글을 작성하거나 수정할 때 요청 본문(JSON)에서 데이터를 받는 역할
 * - Entity를 직접 받지 않고 DTO를 사용하는 이유:
 *      1) 보안 향상
 *      2) 엔티티 변경에 따른 API 변경 방지
 *      3) 데이터 검증을 DTO에서 분리 가능
 */

@Getter
@NoArgsConstructor

public class QnaRequestDto {
    private String title;   // 게시글 제목
    private String content; // 게시글 내용
    private String writer;  // 작성자 이름
}

