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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenreType genre;

    @Column(nullable = false, length = 100)
    private String bookTitle;

    @Column(length = 255)
    private String publisher;

    private LocalDateTime publishedDate;

    @Column(nullable = false, length = 13)
    private String isbn;

    private Integer pageCount;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer roomCount;

    @Column(nullable = false, length = 50)
    private String authorName;

    @Builder.Default
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean bestSeller; //베스트셀러 여부

    @Builder
    public Book(Long bookId, GenreType genre, String bookTitle, String publisher, LocalDateTime publishedDate, String isbn, Integer pageCount, String description, Integer roomCount, String authorName, String imageUrl, boolean bestSeller) {
        this.bookId = bookId;
        this.genre = genre;
        this.bookTitle = bookTitle;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.description = description;
        this.roomCount = roomCount;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
        this.bestSeller = bestSeller;
    }

    public void addFavorite(Favorite favorite) {
        this.favorites.add(favorite);
        favorite.setBook(this);
    }
}
