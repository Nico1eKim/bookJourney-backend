package com.example.bookjourneybackend.domain.user.domain.repository;

import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteGenreRepository extends JpaRepository<FavoriteGenre, Long> {
}
