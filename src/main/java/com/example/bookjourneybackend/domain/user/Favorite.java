package com.example.bookjourneybackend.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorites")
@Getter
@NoArgsConstructor
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //todo Book @ManyToOne으로 바꾸기
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Builder
    public Favorite(Long favoriteId, User user, Long bookId) {
        this.favoriteId = favoriteId;
        this.user = user;
        this.bookId = bookId;
    }
}
