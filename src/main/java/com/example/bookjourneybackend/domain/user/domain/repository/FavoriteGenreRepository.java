package com.example.bookjourneybackend.domain.user.domain.repository;

import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteGenreRepository extends JpaRepository<FavoriteGenre, Long> {
    Optional<List<FavoriteGenre>> findByGenre(GenreType genre);
}
