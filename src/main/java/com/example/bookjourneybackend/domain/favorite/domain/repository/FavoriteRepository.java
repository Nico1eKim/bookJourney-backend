package com.example.bookjourneybackend.domain.favorite.domain.repository;

import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
