package com.example.bookjourneybackend.domain.room.readTogether;

import com.example.bookjourneybackend.domain.user.User;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "record_likes")
@NoArgsConstructor
@Getter
public class RecordLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public RecordLike(Long recordLikeId, Record record, User user) {
        this.recordLikeId = recordLikeId;
        this.record = record;
        this.user = user;
    }
}
