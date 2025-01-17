package com.example.bookjourneybackend.domain.comment.domain;

import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@NoArgsConstructor
@Getter
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @Column(nullable = false, length = 3000)
    private String content;

    @Builder
    public Comment(Long commentId, Record record, String content) {
        this.commentId = commentId;
        this.record = record;
        this.content = content;
    }
}
