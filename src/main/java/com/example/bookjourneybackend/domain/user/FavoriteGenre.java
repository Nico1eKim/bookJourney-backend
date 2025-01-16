package com.example.bookjourneybackend.domain.user;

import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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

    //todo Genre ManyToOne 추가
    @Column(name = "genre_id", nullable = false)
    private Long genreId;

    @Builder
    public FavoriteGenre(Long favoriteGenreId, User user, Long genreId) {
        this.favoriteGenreId = favoriteGenreId;
        this.user = user;
        this.genreId = genreId;
    }
}
