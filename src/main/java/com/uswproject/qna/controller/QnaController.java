package com.uswproject.qna.controller;

import com.uswproject.qna.dto.QnaRequestDto;
import com.uswproject.qna.dto.QnaResponseDto;
import com.uswproject.qna.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QnaController
 * - HTTP 요청을 받아서 Service에게 전달
 * - Service 결과를 JSON으로 응답
 * - Controller는 절대 로직을 가지지 않고 전달자 역할만 수행
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna") // 공통 URL prefix

public class QnaController {
    private final QnaService qnaService;

    /**
     * 게시글 생성
     * POST /api/qna
     */
    @PostMapping
    public QnaResponseDto create(@RequestBody QnaRequestDto dto) {
        return qnaService.create(dto);
    }

    /**
     * 단일 게시글 조회
     * GET /api/qna/{id}
     */
    @GetMapping("/{id}")
    public QnaResponseDto getOne(@PathVariable Long id) {
        return qnaService.getOne(id);
    }

    /**
     * 전체 목록 조회
     * GET /api/qna
     */
    @GetMapping
    public List<QnaResponseDto> getList() {
        return qnaService.getList();
    }

    /**
     * 게시글 수정
     * PUT /api/qna/{id}
     */
    @PutMapping("/{id}")
    public QnaResponseDto update(@PathVariable Long id, @RequestBody QnaRequestDto dto) {
        return qnaService.update(id, dto);
    }

    /**
     * 게시글 삭제
     * DELETE /api/qna/{id}
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        qnaService.delete(id);
        return "삭제 완료";
    }
}
