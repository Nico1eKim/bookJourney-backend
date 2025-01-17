package com.example.bookjourneybackend.domain.room.domain;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@NoArgsConstructor
@Getter
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column(nullable = false)
    private Double userPercentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer currentPage;


    @Builder
    public Room(Long roomId, RoomType roomType, UserRole userRole, Double userPercentage, User user, Book book) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.userRole = userRole;
        this.userPercentage = userPercentage;
        this.user = user;
        this.book = book;
    }
}
