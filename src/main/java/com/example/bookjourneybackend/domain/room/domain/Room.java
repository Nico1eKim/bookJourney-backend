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
//@Table(name = "rooms")
@Table(name = "rooms", indexes = @Index(name = "idx_room_name", columnList = "room_name"))
@AllArgsConstructor
@NoArgsConstructor
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Column(length = 60)
    private String roomName;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private boolean isPublic;

    private Integer password;

    @Setter
    @Column(nullable = false)
    private Double roomPercentage;

    private LocalDate startDate;    //방을 생성한 시점 = 방의 모집 시작 기간 = 방의 시작 기간

    @Setter
    private LocalDate progressEndDate;

    private LocalDate recruitEndDate;

    @Column(nullable = false)
    private Integer recruitCount;

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoom> userRooms = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Record> records = new ArrayList<>();

    public static Room makeReadTogetherRoom(String roomName, Book book, boolean isPublic, Integer password, LocalDate startDate, LocalDate progressEndDate, LocalDate recruitEndDate, Integer recruitCount) {
        return Room.builder()
                .roomType(RoomType.TOGETHER)
                .roomName(roomName)
                .book(book)
                .isPublic(isPublic)
                .password(password)
                .roomPercentage(0.0)
                .startDate(startDate)
                .progressEndDate(progressEndDate)
                .recruitEndDate(recruitEndDate)
                .recruitCount(recruitCount)
                .build();
    }

    public static Room makeReadAloneRoom(Book book) {
        return Room.builder()
                .roomType(RoomType.ALONE)
                .book(book)
                .startDate(LocalDate.now())
                .roomPercentage(0.0)
                .recruitCount(1)
                .build();
    }

    public void addUserRoom(UserRoom userRoom) {
        this.userRooms.add(userRoom);
        userRoom.setRoom(this);
    }

    public void addRecord(Record record) {
        this.records.add(record);
        record.setRoom(this);
    }

    public void updateRoomPercentage(double roomPercentage) {
        this.roomPercentage = roomPercentage;
    }
}
