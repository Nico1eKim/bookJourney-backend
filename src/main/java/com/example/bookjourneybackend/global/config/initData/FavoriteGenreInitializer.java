package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.FavoriteGenreRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FavoriteGenreInitializer {

    private final FavoriteGenreRepository favoriteGenreRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional // 트랜잭션 추가
    public void initializeFavoriteGenres() {
        List<User> users = userRepository.findAll(); // User 리스트 로드
        Optional<List<Book>> books = bookRepository.findByBestSellerTrue(); // 베스트셀러리스트 로드

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i); // 특정 User 가져오기
            Book book = books.get().get(i % books.get().size()); // 특정 Book 가져오기

            FavoriteGenre favoriteGenre = FavoriteGenre.builder()
                    .user(user)
                    .book(book)
                    .genre(book.getGenre())
                    .build();

            // 연관관계 설정
            user.addFavoriteGenres(favoriteGenre);

            // FavoriteGenre 저장
            favoriteGenreRepository.save(favoriteGenre);
        }
    }
}
