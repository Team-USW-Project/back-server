package com.uswproject.qna.repository;

import com.uswproject.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * QnaRepository
 * - DB와 직접 소통하는 영역
 * - JpaRepository<Qna, Long> 상속하면
 *      save(), findById(), findAll(), deleteById() 등 기본 CRUD 메소드 자동 제공
 */

@Repository

public interface QnaRepository extends JpaRepository<Qna, Long> {
}
