package com.example.bookjourneybackend.domain.user.domain;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "favorite_genres")
@Getter
@NoArgsConstructor
public class FavoriteGenre extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_genre_id")
    private Long favoriteGenreId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenreType genre;

    @Builder
    public FavoriteGenre(Long favoriteGenreId, User user, Book book, GenreType genre) {
        this.favoriteGenreId = favoriteGenreId;
        this.user = user;
        this.book = book;
        this.genre = genre;
    }
}
