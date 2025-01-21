package com.example.bookjourneybackend.domain.book.service;

import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookInfoResponse;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.AladinApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.ALADIN_API_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCacheService {

    private final RestTemplate restTemplate;
    private final AladinApiUtil aladinApiUtil;

    //Ex) books:searchTerm:해리포터(인코딩 안된상태로 들어감):genreType:NOVEL_POETRY_DRAMA:queryType:Title:page:1
    @Cacheable(
            cacheNames = "getBooks",
            key = "'books:searchTerm:' + #p0?.searchTerm + " +
                    "':genreType:' + #p0?.genreType + " +
                    "':queryType:' + #p0?.queryType + " +
                    "':page:' + #p0?.page",
            cacheManager = "bookCacheManager"
    )
    public String getCurrentPage(GetBookListRequest getBookListRequest) {
        log.info("[getCurrentPage Caching] 검색어: {}, 장르명: {}, 검색종류: {}, 페이지수: {}",
                getBookListRequest.getSearchTerm(), getBookListRequest.getGenreType(), getBookListRequest.getQueryType(), getBookListRequest.getPage());

        String requestUrl = aladinApiUtil.buildApiUrl(getBookListRequest);
        String currentResponse;

        try {
            currentResponse = restTemplate.getForEntity(requestUrl, String.class).getBody();
            aladinApiUtil.checkValidatedResponse(currentResponse);
            log.info("알라딘 API 응답 Body: {}", currentResponse);

        } catch (RestClientException e) {
            throw new GlobalException(ALADIN_API_ERROR);
        }


        return currentResponse;
    }

    @Cacheable(
            cacheNames = "getBookInfo",
            key = "'books:isbn:' + #p2",
            cacheManager = "bookInfoCacheManager"
    )
    public GetBookInfoResponse cachingBookInfo(String title, String author, String isbn, String cover, String description, String categoryName, String publisher, String publishedDate) {
        return new GetBookInfoResponse(categoryName, cover, title, author, publisher, publishedDate, isbn, description);
    }

    @Cacheable(
            cacheNames = "getBookInfo",
            key = "'books:isbn:' + #p0",
            cacheManager = "bookInfoCacheManager"
    )
    public GetBookInfoResponse checkBookInfo(String isbn) {

        return null;
    }
}
