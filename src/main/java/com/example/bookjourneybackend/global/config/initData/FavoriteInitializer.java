package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import com.example.bookjourneybackend.domain.favorite.domain.repository.FavoriteRepository;
import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_BESTSELLER;

@Component
@RequiredArgsConstructor
public class FavoriteInitializer {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    Random random = new Random();

    @Transactional // 트랜잭션 추가
    public void initializeFavorites() {
        List<User> users = userRepository.findAll(); // User 리스트 로드
        List<Book> books = bookRepository.findAll(); // Book 리스트 로드

        int favoriteCount;

        for (User user : users) {

            favoriteCount = random.nextInt(10) + 1; //사용자당 즐겨찾기 10개 사이 랜덤

            for (int i = 0; i < favoriteCount; i++) {

                Book book = books.get(i);

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
}
