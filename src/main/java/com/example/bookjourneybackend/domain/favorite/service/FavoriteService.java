package com.example.bookjourneybackend.domain.favorite.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.book.dto.response.BookInfo;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookInfoResponse;
import com.example.bookjourneybackend.domain.book.service.BookCacheService;
import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import com.example.bookjourneybackend.domain.favorite.domain.dto.response.GetFavoriteListResponse;
import com.example.bookjourneybackend.domain.favorite.domain.dto.response.PostFavoriteAddResponse;
import com.example.bookjourneybackend.domain.favorite.domain.repository.FavoriteRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;
import java.util.Collections;
import java.util.List;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_BOOK;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_USER;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookCacheService bookCacheService;
    private final DateUtil dateUtil;

    /**
     * isbn에 해당하는 책을 즐겨찾기에 추가
     * 1. 해당책이 이미 즐겨찾기 되어있는 지 검사
     * 2. DB에 존재하면 DB에 존재하는 책을 바로 즐겨찾기 테이블에 추가
     * 3. 존재하지 않는 다면 캐시저장소에서 정보 찾아와서 DB저장후 즐겨찾기 테이블에 추가
     * @param isbn,userId
     * @return PostFavoriteAddResponse
     */
    public PostFavoriteAddResponse addFavorite(String isbn, Long userId) {
        log.info("[FavoriteService.addFavorite]");

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        // 이미 즐겨찾기에 등록되어 있는지 확인
        if (favoriteRepository.existsFavoriteByUserIdAndIsbn(userId, isbn)) {
            throw new GlobalException(CANNOT_FAVORITE);
        }

        //DB에 책 저장확인 없으면 캐시 저장소에서 확인
        Book findBook = bookRepository.findByIsbn(isbn)
                .orElseGet(() -> {
                    //TODO bookCacheService.checkBookInfo retrun 형태 book으로 바뀌면 코드 리펙
                    GetBookInfoResponse getBookInfoResponse = bookCacheService.checkBookInfo(isbn);

                    if (getBookInfoResponse == null) {
                        throw new GlobalException(CANNOT_FOUND_BOOK);
                    }
                    getBookInfoResponse.setFavorite(true);
                    Book book = Book.builder()
                            .isbn(isbn)
                            .bookTitle(getBookInfoResponse.getBookTitle())
                            .authorName(getBookInfoResponse.getAuthorName())
                            .genre(GenreType.parsingGenreType(getBookInfoResponse.getGenre()))
                            .publisher(getBookInfoResponse.getPublisher())
                            .publishedDate(dateUtil.parseDateToLocalDateFromPublishedDateString(getBookInfoResponse.getPublishedDate()))
                            .description(getBookInfoResponse.getDescription())
                            .imageUrl(getBookInfoResponse.getImageUrl())
                            .bestSeller(false)
                            .pageCount(null)
                            .build();

                    //캐시 저장소에서 찾은 책 DB저장
                    bookRepository.save(book);
                    return book;
                });

        //즐겨찾기 추가
        favoriteRepository.save(Favorite.builder()
                .book(findBook)
                .user(user)
                .build());

        return PostFavoriteAddResponse.of(findBook.getBookId(),true);
    }

    /**
     * 해당 유저의 즐겨찾기 리스트 조회
     * @param userId
     * @return GetFavoriteListResponse
     */
    public GetFavoriteListResponse showFavoriteList(Long userId) {
        log.info("[FavoriteService.showFavoriteList]");

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        //즐겨찾기 리스트 조회
        List<BookInfo> bookList = favoriteRepository.findByUserOrderByCreatedAtDesc(user)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(fav -> BookInfo.of(fav.getBook()))
                .toList();

        return GetFavoriteListResponse.of(bookList);
    }

}
