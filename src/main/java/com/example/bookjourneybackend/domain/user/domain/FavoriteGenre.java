package com.example.bookjourneybackend.domain.user.domain;

import com.example.bookjourneybackend.domain.book.domain.Genre;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorite_genres")
@Getter
@NoArgsConstructor
public class FavoriteGenre extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_genre_id")
    private Long favoriteGenreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @Builder
    public FavoriteGenre(Long favoriteGenreId, User user, Genre genre) {
        this.favoriteGenreId = favoriteGenreId;
        this.user = user;
        this.genre = genre;
    }
}
