package com.example.bookjourneybackend.domain.record.domain;

import com.example.bookjourneybackend.domain.readTogether.domain.ReadTogether;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "records")
@NoArgsConstructor
@Getter
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "read_together_id", nullable = false)
    private ReadTogether readTogether;

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

    @Builder
    public Record(Long recordId, ReadTogether readTogether, User user, String recordTitle, RecordType recordType, Integer recordPage, String content) {
        this.recordId = recordId;
        this.readTogether = readTogether;
        this.user = user;
        this.recordTitle = recordTitle;
        this.recordType = recordType;
        this.recordPage = recordPage;
        this.content = content;
    }
}
