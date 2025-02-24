package com.example.bookjourneybackend.domain.book.domain.repository;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b LEFT JOIN b.rooms r GROUP BY b ORDER BY COUNT(r) DESC")
    List<Book> findBookWithMostRooms();

    Optional<List<Book>> findByBestSellerTrue();

    Optional<Book> findByBestSellerTrueAndGenre(GenreType genre);

}
