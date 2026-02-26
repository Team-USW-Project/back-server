package com.uswproject.qna.entity;

import com.uswproject.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "comment_reactions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_comment_reaction_member_comment",
                columnNames = {"member_id", "comment_id"}
        )
)
public class CommentReaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="comment_id", nullable = false)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReactionType type;

    public CommentReaction(Long memberId, Comment comment, ReactionType type) {
        this.memberId = memberId;
        this.comment = comment;
        this.type = type;
    }

    public void changeType(ReactionType type) {
        this.type = type;
    }
}