package com.example.bookjourneybackend.domain.readTogether.domain;

import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "read_together")
public class ReadTogether extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long readTogetherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, length = 60)
    private String roomName;

    @Column(nullable = false)
    private boolean isPublic;

    private Integer password;

    @Column(nullable = false)
    private Double roomPercentage;

    @Column(nullable = false)
    private LocalDateTime progressStartDate;

    @Column(nullable = false)
    private LocalDateTime progressEndDate;

    @Column(nullable = false)
    private LocalDateTime recruitStartDate;

    @Column(nullable = false)
    private LocalDateTime recruitEndDate;

    @Column(nullable = false)
    private Integer recruitCount;

    @Column(nullable = false)
    private Integer recordCount;


    @Builder
    public ReadTogether(Long readTogetherId, Room room, String roomName, boolean isPublic, Integer password, Double roomPercentage, LocalDateTime progressStartDate, LocalDateTime progressEndDate, LocalDateTime recruitStartDate, LocalDateTime recruitEndDate, Integer recruitCount, Integer recordCount) {
        this.readTogetherId = readTogetherId;
        this.room = room;
        this.roomName = roomName;
        this.isPublic = isPublic;
        this.password = password;
        this.roomPercentage = roomPercentage;
        this.progressStartDate = progressStartDate;
        this.progressEndDate = progressEndDate;
        this.recruitStartDate = recruitStartDate;
        this.recruitEndDate = recruitEndDate;
        this.recruitCount = recruitCount;
        this.recordCount = recordCount;
    }
}
