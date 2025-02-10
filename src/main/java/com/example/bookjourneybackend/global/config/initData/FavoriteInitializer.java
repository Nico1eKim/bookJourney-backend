package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import com.example.bookjourneybackend.domain.favorite.domain.repository.FavoriteRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class FavoriteInitializer {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    Random random = new Random();

    @Transactional // 트랜잭션 추가
    public void initializeFavorites() {
        List<User> users = userRepository.findAll();
        List<Book> books = bookRepository.findAll();

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
