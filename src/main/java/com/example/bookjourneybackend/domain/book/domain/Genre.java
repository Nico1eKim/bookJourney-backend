package com.example.bookjourneybackend.domain.book.domain;

import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "genres")
@Getter
@NoArgsConstructor
public class Genre extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genreId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenreType genre;

    @Builder
    public Genre(Long genreId, GenreType genre) {
        this.genreId = genreId;
        this.genre = genre;
    }
}
