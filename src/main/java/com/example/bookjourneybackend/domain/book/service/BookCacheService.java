package com.example.bookjourneybackend.domain.book.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookInfoResponse;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.AladinApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.ALADIN_API_ERROR;

@Service
@RequiredArgsConstructor
public class BookCacheService {

    private final RestTemplate restTemplate;
    private final AladinApiUtil aladinApiUtil;
    private final ObjectMapper objectMapper;

    //Ex) books:searchTerm:해리포터(인코딩 안된상태로 들어감):genreType:NOVEL_POETRY_DRAMA:queryType:Title:page:1
    @Cacheable(
            cacheNames = "getBooks",
            key = "'books:searchTerm:' + #p0?.searchTerm + " + "':genreType:' + #p0?.genreType + " + "':queryType:' + #p0?.queryType + " + "':page:' + #p0?.page",
            cacheManager = "bookCacheManager"
    )
    public String getCurrentPage(GetBookListRequest getBookListRequest) {

        String requestUrl = aladinApiUtil.buildSearchApiUrl(getBookListRequest);
        String currentResponse;

        try {
            currentResponse = restTemplate.getForEntity(requestUrl, String.class).getBody();
            aladinApiUtil.checkValidatedResponse(currentResponse);
        } catch (RestClientException e) {
            throw new GlobalException(ALADIN_API_ERROR);
        }


        return currentResponse;
    }

    //TODO BOOK으로 반환하도록 수정(캐싱되어있는 북을 즐겨찾기에서도 써야하기떄문에 book형태로반환해야함)
    @Cacheable(cacheNames = "getBookInfo", key = "'book:isbn:' + #p2", cacheManager = "bookInfoCacheManager")
    public GetBookInfoResponse cachingBookInfo(String title, String author, String isbn, String cover, String description, String categoryName, String publisher, String publishedDate) {
        return GetBookInfoResponse.of(categoryName, cover, title, author, false, publisher, publishedDate, isbn, description);
    }

    @Cacheable(cacheNames = "getBookInfo", key = "'book:isbn:' + #p0", cacheManager = "bookInfoCacheManager")
    public GetBookInfoResponse checkBookInfo(String isbn) {

        String requestUrl = aladinApiUtil.buildLookUpApiUrl(isbn);
        String currentResponse = aladinApiUtil.requestBookInfoFromAladinApi(requestUrl);

        Book book = aladinApiUtil.parseAladinApiResponseToBook(currentResponse,false,0);

        return GetBookInfoResponse.of(
                book.getGenre().getGenreType(),
                book.getImageUrl(),
                book.getBookTitle(),
                book.getAuthorName(),
                false,
                book.getPublisher(),
                book.getPublishedDate().toString(),
                book.getIsbn(),
                book.getDescription()
        );

    }
}
