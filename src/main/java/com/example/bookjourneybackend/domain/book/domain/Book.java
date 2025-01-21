package com.example.bookjourneybackend.domain.book.domain;

import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "books")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @Column(nullable = false, length = 100)
    private String bookTitle;

    @Column(length = 255)
    private String publisher;

    private LocalDateTime publishedDate;

    @Column(nullable = false, length = 13)
    private String isbnCode;

    private Integer pageCount;

    @Column(length = 1000)
    private String description;

    private Integer roomCount;

    @Column(nullable = false, length = 50)
    private String authorName;

    @OneToMany(mappedBy = "book")
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    @Builder
    public Book(Long bookId, Genre genre, String bookTitle, String publisher, LocalDateTime publishedDate, String isbnCode, Integer pageCount, String description, Integer roomCount, String authorName) {
        this.bookId = bookId;
        this.genre = genre;
        this.bookTitle = bookTitle;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isbnCode = isbnCode;
        this.pageCount = pageCount;
        this.description = description;
        this.roomCount = roomCount;
        this.authorName = authorName;
    }

    public void addFavorite(Favorite favorite) {
        this.favorites.add(favorite);
    }
}
