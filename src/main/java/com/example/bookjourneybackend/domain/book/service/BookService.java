package com.example.bookjourneybackend.domain.book.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.domain.book.dto.response.*;
import com.example.bookjourneybackend.domain.favorite.domain.repository.FavoriteRepository;
import com.example.bookjourneybackend.domain.recentSearch.service.RecentSearchService;
import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.example.bookjourneybackend.global.entity.EntityStatus.ACTIVE;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final FavoriteRepository favoriteRepository;
    private final ObjectMapper objectMapper;
    private final BookCacheService bookCacheService;
    private final UserRepository userRepository;

    private final RecentSearchService recentSearchService;

    /**
     * 1. Redis에 있는지 확인하고 있으면 Redis에 value로 반환
     * 2. RestTemplate을 이용해 동기적으로 현재 페이지 불러와서 Redis에 저장 후 응답 전송
     * 3. RestTemplate을 이용해 비동기적으로 다음 페이지를 불러와서 Redis에 저장
     * Thread.start() 대신 CompletableFuture를 이용한 이유 : Thread Pool을 사용해 자원을 효율적으로 관리
     * @param getBookListRequest
     * @return
     */
    //todo Thread Pool Monitoring 로그 출력
    @Transactional
    public GetBookListResponse searchBook(GetBookListRequest getBookListRequest, Long userId) {

        recentSearchService.addRecentSearch(userId, getBookListRequest.getSearchTerm());

        // 현재 페이지 데이터 가져오기
        String currentResponse = bookCacheService.getCurrentPage(getBookListRequest);
        log.info("currentResponse : {}", currentResponse);

        // 비동기적으로 다음 페이지 캐싱
        CompletableFuture.runAsync(() -> {
            bookCacheService.getCurrentPage(getBookListRequest.IncreasePage());
        });

        //응답 JSON 데이터 파싱
        List<BookInfo> bookList = parseBookListFromResponse(currentResponse);

        return GetBookListResponse.of(bookList);
    }

    private List<BookInfo> parseBookListFromResponse(String currentResponse) {
        List<BookInfo> bookList = new ArrayList<>();
        try{
            //JSON 형식 오류 허용 -> "Unrecognized character escape ''' (code 39)" 에러 해결용
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//            currentResponse = currentResponse.replace("'", "\"");

            JsonNode root = objectMapper.readTree(currentResponse);
            String searchCategoryName = root.get("searchCategoryName").asText();

            JsonNode items = root.get("item");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    String title = StringEscapeUtils.unescapeHtml4(item.get("title").asText());
                    String author = item.get("author").asText();

                    //isbn 13자리가 비어있는 경우 10자리 사용
                    String isbn = item.has("isbn13") && !item.get("isbn13").asText().isEmpty()
                            ? item.get("isbn13").asText()
                            : item.get("isbn").asText();

                    if (isbn.startsWith("K")) { //책이 아닌 경우(ex. 굿즈) 책 목록에서 제거
                        continue;
                    }

                    String cover = item.get("cover").asText();

                    String description = item.get("description").asText();

                    String publisher = item.get("publisher").asText();
                    String publishedDate = item.get("pubDate").asText();

                    String categoryName = item.get("categoryName").asText();
                    GenreType genreType = (searchCategoryName == null || searchCategoryName.isBlank() || searchCategoryName.isEmpty() || searchCategoryName.equals("전체"))?
                            GenreType.parsingGenreType(categoryName) : GenreType.parsingGenreType(searchCategoryName);

                    GetBookInfoResponse g = bookCacheService.cachingBookInfo(title, author, isbn, cover, description, genreType.getGenreType(), publisher, publishedDate);

                    bookList.add(new BookInfo(g.getBookTitle(), g.getAuthorName(), g.getIsbn(), g.getImageUrl()));
                }
            }
        } catch (JsonProcessingException e) {
            throw new GlobalException(ALADIN_API_PARSING_ERROR);
        }
        return bookList;
    }

    /**
     * 책 상세정보 조회
     */
    public GetBookInfoResponse showBookInfo(String isbn, Long userId) {
        GetBookInfoResponse getBookInfoResponse = bookCacheService.checkBookInfo(isbn);

        Optional<Book> findBook = bookRepository.findByIsbn(isbn);

        //레포지토리에 책이 존재하면..
        findBook.ifPresent(book -> {
            boolean isFavorite = favoriteRepository.existsActiveFavoriteByUserIdAndBook(userId, book);
            getBookInfoResponse.setFavorite(isFavorite);
        });

        bookCacheService.checkBookInfo(isbn);

        return getBookInfoResponse;
    }

    /**
     * 읽기횟수가 가장 많은 책 조회
     */
    public GetBookPopularResponse showPopularBook() {
        return bookRepository.findBookWithMostRooms().stream()
                .findFirst()
                .map(book -> GetBookPopularResponse.of(
                        book.getBookId(),
                        book.getIsbn(),
                        book.getBookTitle(),
                        book.getImageUrl(),
                        book.getAuthorName(),
                        book.getRooms().size(),
                        book.getDescription()
                ))
                .orElse(null);
    }


    /**
     * 로그인 한 유저의 베스트셀러 정보 조회
     * @param userId
     * @return GetBookBestSellersResponse
     */
    public GetBookBestSellersResponse showBestSellers(Long userId) {

        //로그인 한 유저 찾기
        User user = userRepository.findByUserIdAndStatus(userId, ACTIVE)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));
        //유저의 관심장르 찾기
        List<FavoriteGenre> favoriteGenres = user.getFavoriteGenres();

        List<BestSellerInfo> bestSellerInfoList = new ArrayList<>();

        //유저의 관심장르 별 베스트셀러 정보 불러오기
        for (FavoriteGenre favoriteGenre : favoriteGenres) {
            Book bestseller = bookRepository.findByBestSellerTrueAndGenre(favoriteGenre.getGenre())
                    .orElseThrow(() -> new GlobalException(CANNOT_FOUND_BESTSELLER));

            BestSellerInfo bestSellerInfo = new BestSellerInfo(bestseller.getImageUrl(),bestseller.getIsbn(),bestseller.getGenre().getGenreType());
            bestSellerInfoList.add(bestSellerInfo);
        }
        return GetBookBestSellersResponse.of(bestSellerInfoList,user.getNickname());

    }

    @Transactional
    public void deleteBook(Book book) {
        if(book.getRooms().isEmpty() && book.getFavorites().isEmpty()){
            bookRepository.delete(book);
        }
    }


}
