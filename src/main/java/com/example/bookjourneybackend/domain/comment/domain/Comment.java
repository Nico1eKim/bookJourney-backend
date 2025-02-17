package com.example.bookjourneybackend.domain.comment.domain;

import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10000)
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();

    @Builder
    public Comment(Long commentId, Record record, User user, String content) {
        this.commentId = commentId;
        this.record = record;
        this.user = user;
        this.content = content;
    }

    public void addCommentLike(CommentLike commentLike) {
        this.commentLikes.add(commentLike);
        commentLike.setComment(this);
    }
}
