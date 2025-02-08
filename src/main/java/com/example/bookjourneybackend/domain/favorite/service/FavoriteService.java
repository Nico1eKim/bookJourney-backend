package com.example.bookjourneybackend.domain.favorite.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.book.dto.response.BookInfo;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookInfoResponse;
import com.example.bookjourneybackend.domain.book.service.BookCacheService;
import com.example.bookjourneybackend.domain.book.service.BookService;
import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import com.example.bookjourneybackend.domain.favorite.domain.dto.request.DeleteFavoriteSelectedRequest;
import com.example.bookjourneybackend.domain.favorite.domain.dto.response.FavoriteInfo;
import com.example.bookjourneybackend.domain.favorite.domain.dto.response.GetFavoriteListResponse;
import com.example.bookjourneybackend.domain.favorite.domain.dto.response.PostFavoriteAddResponse;
import com.example.bookjourneybackend.domain.favorite.domain.repository.FavoriteRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.DateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_BOOK;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_USER;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookCacheService bookCacheService;
    private final DateUtil dateUtil;
    private final BookService bookService;
    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * isbn에 해당하는 책을 즐겨찾기에 추가
     * 1. 해당책이 이미 즐겨찾기 되어있는 지 검사
     * 2. DB에 존재하면 DB에 존재하는 책을 바로 즐겨찾기 테이블에 추가
     * 3. 존재하지 않는 다면 캐시저장소에서 정보 찾아와서 DB저장후 즐겨찾기 테이블에 추가
     * @param isbn,userId
     * @return PostFavoriteAddResponse
     */
    public PostFavoriteAddResponse addFavorite(String isbn, Long userId) {

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
    @Transactional(readOnly = true)
    public GetFavoriteListResponse showFavoriteList(Long userId) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        //즐겨찾기 리스트 조회
        List<FavoriteInfo> favoriteList = favoriteRepository.findByUserOrderByCreatedAtDesc(user)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(FavoriteInfo::of)
                .toList();

        return GetFavoriteListResponse.of(favoriteList);
    }

    /**
     * 해당 유저가 선택한 즐겨찾기 삭제
     * 1.삭제할 즐겨찾기 선택되었는지 검증(리스트가 비어서 요청보내진건지 검증)
     * 2.존재하는 즐겨찾기인지 검증
     * 3.요청을 보낸 사용자가 등록한 즐겨찾기인지 검증
     * 4.선택한 즐겨찾기 전체삭제
     * 5.선택한 즐겨찾기의 책에해당하는 방이 없거나, 이책을 즐겨찾기 한사람이 없으면 db에서 삭제
     * @param deleteFavoriteSelectedRequest,userId
     */
    public Void deleteSelectedFavorite(DeleteFavoriteSelectedRequest deleteFavoriteSelectedRequest, Long userId) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        // 삭제할 favoriteId 리스트 추출
        List<Long> favoriteIds = deleteFavoriteSelectedRequest.getFavoriteIds();

        // 삭제할 즐겨찾기가 선택되지않음
        if (favoriteIds.isEmpty()) {
            throw new GlobalException(NOT_SELECTED_FAVORITE);
        }

        // 존재하는 즐겨찾기인지 확인
        List<Favorite> favorites = favoriteRepository.findAllById(favoriteIds);
        if (favorites.isEmpty()) {
            throw new GlobalException(CANNOT_FOUND_FAVORITE);
        }

        // 사용자 소유의 즐겨찾기인지 검증
        if (favorites.stream().anyMatch(favorite -> !favorite.getUser().equals(user))) {
            throw new GlobalException(CANNOT_DELETE_FAVORITE);
        }

        // 삭제할 Favorite이 참조하는 Book 목록 가져오기
        Set<Book> booksToCheck = favorites.stream()
                .map(Favorite::getBook)
                .collect(Collectors.toSet());

        // 즐겨찾기 삭제 (Batch 처리)
        favoriteRepository.deleteAllByIdInBatch(favoriteIds);

        // 삭제할 수 있는 Book 삭제
        for (Book book : booksToCheck) {
            bookService.deleteBook(book);
        }

        // 영속성 컨텍스트 초기화하여 최신 상태 반영
        entityManager.flush();
        entityManager.clear();

        return null;
    }


    /**
     * isbn에 해당하는 책을 즐겨찾기에서 삭제
     * 1. 해당책이 이미 즐겨찾기 되어있는지 검사
     * 2. 즐겨찾기 중이 아니라면 예외
     * 3. 즐겨찾기 삭제 후, 책이 삭제할수 있는 상태라면 db에서 삭제
     * @param isbn,userId
     * @return PostFavoriteAddResponse
     */
    public PostFavoriteAddResponse deleteFavorite(String isbn, Long userId) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        // 즐겨찾기 중인지 검사
        if (!favoriteRepository.existsFavoriteByUserIdAndIsbn(userId, isbn)) {
            throw new GlobalException(CANNOT_DELETE_FAVORITE);
        }

        //즐겨찾기 한 책 찾기
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_BOOK));

        //즐겨찾기에서 삭제
        favoriteRepository.deleteFavoriteByUserAndBook(user,book);
        entityManager.flush();

        //즐겨찾기한 책 db에서 삭제
        bookService.deleteBook(book);

        return PostFavoriteAddResponse.of(book.getBookId(),false);
    }
}
