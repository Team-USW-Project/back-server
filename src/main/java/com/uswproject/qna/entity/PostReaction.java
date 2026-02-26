package com.uswproject.qna.entity;

import com.uswproject.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "post_reaction",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_reaction_member_post",
                columnNames = {"member_id", "post_id"}
        )
)
public class PostReaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false) // ✅ 콜론 제거
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;

    public PostReaction(Long memberId, Post post, ReactionType type) {
        this.memberId = memberId;
        this.post = post;
        this.type = type;
    }

    public void changeType(ReactionType type) {
        this.type = type;
    }
}