package com.uswproject.qna.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Qna Entity
 * - 실제 DB의 qna 테이블과 매핑되는 클래스
 * - JPA가 테이블 자동 생성
 */

@Entity                 // JPA 엔티티 선언
@Table(name = "qna")    // 테이블 이름 명시
@Getter
@Setter
@NoArgsConstructor

public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // PK (자동 증가)

    @Column(nullable = false)
    private String title;   // 게시글 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 게시글 내용 (TEXT 타입으로 저장)

    @Column(nullable = false)
    private String writer;  // 작성자

    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간

    /**
     * @PrePersist
     * - 엔티티가 처음 저장될 때 자동으로 실행
     * - createdAt / updatedAt 자동 초기화
     */

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * @PreUpdate
     * - 엔티티가 업데이트 될 때 실행됨
     * - updatedAt 자동 갱신
     */

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

