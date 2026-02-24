package com.uswproject.qna.entity;

import com.uswproject.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 게시글의 댓글?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 작성자
    @Column(nullable = false)
    private Long memberId;

    // 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public Comment(Post post, Long memberId, String content) {
        this.post = post;
        this.memberId = memberId;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }
}