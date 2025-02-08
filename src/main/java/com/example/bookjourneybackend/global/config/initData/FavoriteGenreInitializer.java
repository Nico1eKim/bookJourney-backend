package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.recentSearch.domain.RecentSearch;
import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.FavoriteGenreRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import jakarta.transaction.Transactional;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_BESTSELLER;

@Component
@RequiredArgsConstructor
public class FavoriteGenreInitializer {

    private final FavoriteGenreRepository favoriteGenreRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    List<String> favoriteGenres = new ArrayList<>();
    Random random = new Random();

    @Transactional // 트랜잭션 추가
    public void initializeFavoriteGenres() {

        List<User> users = userRepository.findAll(); // User 리스트 로드

        for (User user : users) {
            favoriteGenres = getRandomGenreNames();
            favoriteGenres.forEach(genre -> {
                FavoriteGenre favoriteGenre = FavoriteGenre.builder()
                        .genre(GenreType.fromGenreType(genre)) //장르 정보 매핑
                        .book(bookRepository.findByBestSellerTrueAndGenre(GenreType.fromGenreType(genre))
                                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_BESTSELLER))) //책 정보 매핑
                        .build();
                user.addFavoriteGenres(favoriteGenre); // 유저 정보 매핑
            });
            userRepository.save(user);
        }

    }

    //사용자당 랜덤 장르 3개매핑
    private List<String> getRandomGenreNames() {
        List<GenreType> validGenres = Arrays.stream(GenreType.values())
                .filter(genre -> genre != GenreType.UNKNOWN) // UNKNOWN 제외
                .toList();

        return IntStream.range(0, 3)
                .mapToObj(i -> validGenres.get(random.nextInt(validGenres.size())).getGenreType())
                .collect(Collectors.toList());
    }


}
