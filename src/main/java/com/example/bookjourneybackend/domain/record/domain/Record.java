package com.example.bookjourneybackend.domain.record.domain;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "records")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 90)
    private String recordTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType recordType;

    @Column(nullable = false)
    private Integer recordPage;

    @Column(nullable = false, length = 3000)
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Record(Long recordId, Room room, User user, String recordTitle, RecordType recordType, Integer recordPage, String content) {
        this.recordId = recordId;
        this.room = room;
        this.user = user;
        this.recordTitle = recordTitle;
        this.recordType = recordType;
        this.recordPage = recordPage;
        this.content = content;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setRecord(this);
    }

}
