package com.uswproject.qna.entity;

import com.uswproject.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
    private Long memberId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //이미지 첨부
   /* @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<PostImage> images = new ArrayList<>(); */


    public Post(Long memberId, String title, String content) {
        this.memberId = memberId;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /* public void addImage(PostImage image) {
        this.images.add(image);
    } */


}
