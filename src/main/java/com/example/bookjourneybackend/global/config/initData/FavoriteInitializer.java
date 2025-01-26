package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import com.example.bookjourneybackend.domain.favorite.domain.repository.FavoriteRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FavoriteInitializer {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional // 트랜잭션 추가
    public void initializeFavorites() {
        List<User> users = userRepository.findAll(); // User 리스트 로드
        List<Book> books = bookRepository.findAll(); // Book 리스트 로드

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i); // 특정 User 가져오기
            Book book = books.get(i % books.size()); // 특정 Book 가져오기

            Favorite favorite = Favorite.builder()
                    .user(user)
                    .book(book)
                    .build();

            // 연관관계 설정
            book.addFavorite(favorite);
            user.addFavorite(favorite);

            // Favorite 저장
            favoriteRepository.save(favorite);
        }
    }
}
