package com.example.bookjourneybackend.domain.book.service;

import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookInfoResponse;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.AladinApiUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.ALADIN_API_ERROR;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.ALADIN_API_PARSING_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCacheService {

    private final RestTemplate restTemplate;
    private final AladinApiUtil aladinApiUtil;
    private final ObjectMapper objectMapper;

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

        String requestUrl = aladinApiUtil.buildSearchApiUrl(getBookListRequest);
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
            key = "'book:isbn:' + #p2",
            cacheManager = "bookInfoCacheManager"
    )
    public GetBookInfoResponse cachingBookInfo(String title, String author, String isbn, String cover, String description, String categoryName, String publisher, String publishedDate) {
        return GetBookInfoResponse.of(categoryName, cover, title, author, false, publisher, publishedDate, isbn, description);
    }

    @Cacheable(
            cacheNames = "getBookInfo",
            key = "'book:isbn:' + #p0",
            cacheManager = "bookInfoCacheManager"
    )
    public GetBookInfoResponse checkBookInfo(String isbn) {
        log.info("[checkBookInfo Caching] isbn: {}",
                isbn);

        String requestUrl = aladinApiUtil.buildLookUpApiUrl(isbn);
        String currentResponse;
        GetBookInfoResponse getBookInfoResponse = null;

        try {
            currentResponse = restTemplate.getForEntity(requestUrl, String.class).getBody();
            aladinApiUtil.checkValidatedResponse(currentResponse);
            log.info("알라딘 API 응답 Body: {}", currentResponse);

            //JSON 형식 오류 허용 -> "Unrecognized character escape ''' (code 39)" 에러 해결용
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//            currentResponse = currentResponse.replace("'", "\"");

            JsonNode root = objectMapper.readTree(currentResponse);
            JsonNode items = root.get("item");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    String title = item.get("title").asText();
                    String author = item.get("author").asText();

                    //isbn 13자리가 비어있는 경우 10자리 사용
//                    String isbn = item.has("isbn13") && !item.get("isbn13").asText().isEmpty()
//                            ? item.get("isbn13").asText()
//                            : item.get("isbn").asText();
                    String cover = item.get("cover").asText();

//                    String link = item.get("link").asText();
                    String description = item.get("description").asText();
                    String categoryName = item.get("categoryName").asText();
                    String publisher = item.get("publisher").asText();
                    String publishedDate = item.get("pubDate").asText();

                    getBookInfoResponse = GetBookInfoResponse.of(categoryName, cover, title, author, false, publisher, publishedDate, isbn, description);
                }
            }
        } catch (RestClientException e) {
            throw new GlobalException(ALADIN_API_ERROR);
        } catch (JsonProcessingException e) {
            log.info("Json 파싱 에러 메시지: {}", e.getMessage());
            throw new GlobalException(ALADIN_API_PARSING_ERROR);
        }

        return getBookInfoResponse;
    }
}
