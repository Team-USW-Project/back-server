package com.uswproject.qna.service;


import com.uswproject.qna.dto.QnaRequestDto;
import com.uswproject.qna.dto.QnaResponseDto;
import com.uswproject.qna.entity.Qna;
import com.uswproject.qna.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * QnaService
 * - 게시글 작성, 조회, 수정, 삭제 등 전체 비즈니스 로직 담당
 * - Controller는 단순히 Service 호출만 하는 구조로 만들기 위해 분리
 */

@Service
@RequiredArgsConstructor // 자동으로 생성자 주입(Lombok)

public class QnaService {
    private final QnaRepository qnaRepository;

    /**
     * 게시글 생성(Create)
     */
    public QnaResponseDto create(QnaRequestDto dto) {

        // 요청 DTO → 엔티티로 변환
        Qna qna = new Qna();
        qna.setTitle(dto.getTitle());
        qna.setContent(dto.getContent());
        qna.setWriter(dto.getWriter());

        // DB 저장
        Qna saved = qnaRepository.save(qna);

        // 저장된 엔티티 → 응답 DTO로 변환해 반환
        return new QnaResponseDto(saved);
    }

    /**
     * 게시글 단건 조회(Read)
     */
    public QnaResponseDto getOne(Long id) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글이 존재하지 않습니다."));
        return new QnaResponseDto(qna);
    }

    /**
     * 게시글 전체 조회(Read)
     */
    public List<QnaResponseDto> getList() {
        return qnaRepository.findAll()
                .stream()                   // 리스트 반복 처리
                .map(QnaResponseDto::new)   // 엔티티 → DTO 변환
                .collect(Collectors.toList());
    }

    /**
     * 게시글 수정(Update)
     */
    public QnaResponseDto update(Long id, QnaRequestDto dto) {

        // 기존 데이터 조회
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글이 존재하지 않습니다."));

        // 값 덮어쓰기
        qna.setTitle(dto.getTitle());
        qna.setContent(dto.getContent());
        qna.setWriter(dto.getWriter());

        // 저장 후 반환
        Qna updated = qnaRepository.save(qna);
        return new QnaResponseDto(updated);
    }

    /**
     * 게시글 삭제(Delete)
     */
    public void delete(Long id) {
        qnaRepository.deleteById(id);
    }
}
