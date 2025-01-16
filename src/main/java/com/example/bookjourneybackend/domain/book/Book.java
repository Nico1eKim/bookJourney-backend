package com.example.bookjourneybackend.domain.book;

import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter
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

    @Column(length = 13)
    private Long isbnCode;

    private Integer pageCount;

    @Column(length = 1000)
    private String description;

    private Integer roomCount;

    @Column(nullable = false, length = 50)
    private String authorName;

    @Builder
    public Book(Long bookId, Genre genre, String bookTitle, String publisher,
                LocalDateTime publishedDate, Long isbnCode, Integer pageCount, String description,
                Integer roomCount, String authorName) {
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
}
