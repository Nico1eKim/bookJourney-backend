package com.example.bookjourneybackend.domain.room;

import com.example.bookjourneybackend.domain.book.Book;
import com.example.bookjourneybackend.domain.user.User;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import com.example.bookjourneybackend.global.entity.RoomType;
import com.example.bookjourneybackend.global.entity.UserRole;
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

//    //TODO 사용자 연관관계 추가
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    //TODO 책 연관관계 추가
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "book_id", nullable = false)
//    private Book book;
//
//
//    @Builder
//    public Room(Long roomId, RoomType roomType, UserRole userRole, Double userPercentage, User user, Book book) {
//        this.roomId = roomId;
//        this.roomType = roomType;
//        this.userRole = userRole;
//        this.userPercentage = userPercentage;
//        this.user = user;
//        this.book = book;
//    }
}
