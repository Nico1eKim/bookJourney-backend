package com.example.bookjourneybackend.domain.room.domain;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@Table(name = "rooms")
@AllArgsConstructor
@NoArgsConstructor
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, length = 60)
    private String roomName;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = true)
    private LocalDateTime lastActivityTime;

    private Integer password;

    @Column(nullable = false)
    private Double roomPercentage;

    @Column(nullable = false)
    private LocalDate startDate;    //방을 생성한 시점 = 방의 모집 시작 기간 = 방의 시작 기간

    @Column(nullable = false)
    private LocalDate progressEndDate;

    @Column(nullable = false)
    private LocalDate recruitEndDate;

    @Column(nullable = false)
    private Integer recruitCount;

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoom> userRooms = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Record> records = new ArrayList<>();

    @Builder
    public Room(Long roomId, String roomName, Book book, boolean isPublic, LocalDateTime lastActivityTime, Integer password, Double roomPercentage, LocalDate startDate, LocalDate progressEndDate, LocalDate recruitEndDate, Integer recruitCount) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.book = book;
        this.isPublic = isPublic;
        this.lastActivityTime = lastActivityTime;
        this.password = password;
        this.roomPercentage = roomPercentage;
        this.startDate = startDate;
        this.progressEndDate = progressEndDate;
        this.recruitEndDate = recruitEndDate;
        this.recruitCount = recruitCount;
    }

    public void addUserRoom(UserRoom userRoom) {
        this.userRooms.add(userRoom);
        userRoom.setRoom(this);
    }

    public void addRecord(Record record) {
        this.records.add(record);
        record.setRoom(this);
    }
}
