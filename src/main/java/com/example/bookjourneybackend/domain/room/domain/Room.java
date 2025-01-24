package com.example.bookjourneybackend.domain.room.domain;

import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "rooms")
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, length = 60)
    private String roomName;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private LocalDateTime lastActivityTime;

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

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoom> userRooms = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Record> records = new ArrayList<>();

    @Builder
    public Room(Long roomId, String roomName, boolean isPublic, LocalDateTime lastActivityTime, Integer password, Double roomPercentage, LocalDateTime progressStartDate, LocalDateTime progressEndDate, LocalDateTime recruitStartDate, LocalDateTime recruitEndDate, Integer recruitCount, Integer recordCount) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.isPublic = isPublic;
        this.lastActivityTime = lastActivityTime;
        this.password = password;
        this.roomPercentage = roomPercentage;
        this.progressStartDate = progressStartDate;
        this.progressEndDate = progressEndDate;
        this.recruitStartDate = recruitStartDate;
        this.recruitEndDate = recruitEndDate;
        this.recruitCount = recruitCount;
        this.recordCount = recordCount;
    }

    public void addUserRoom(UserRoom userRoom) {
        this.userRooms.add(userRoom);
        userRoom.setRoom(this);
    }

    public void addRecord(Record record) {
        this.records.add(record);
    }
}
