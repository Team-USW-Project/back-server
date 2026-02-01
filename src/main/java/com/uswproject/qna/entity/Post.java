package com.uswproject.qna.entity;

import com.uswproject.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "posts")

public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자: member 모듈 없으니 일단 id만 저장
    @Column(nullable = false)
    private Long writerMemberId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;


    public Post(Long writerMemberId, String title, String content) {
        this.writerMemberId = writerMemberId;
        this.title = title;
        this.content = content;
    }


}
