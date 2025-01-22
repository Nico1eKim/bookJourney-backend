package com.example.bookjourneybackend.domain.favorite.domain.repository;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f FROM Favorite f WHERE f.user.userId = :userId AND f.book = :book AND f.status = 'active'")
    Optional<Favorite> findActiveFavoriteByUserIdAndBook(@Param("userId") Long userId, @Param("book") Book book);

    @Query("SELECT COUNT(f) > 0 FROM Favorite f WHERE f.user.userId = :userId AND f.book = :book AND f.status = 'active'")
    boolean existsActiveFavoriteByUserIdAndBook(@Param("userId") Long userId, @Param("book") Book book);
}
