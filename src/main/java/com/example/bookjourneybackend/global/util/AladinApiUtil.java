package com.example.bookjourneybackend.global.util;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.ALADIN_API_ERROR;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.ALADIN_API_PARSING_ERROR;

@Slf4j
@RequiredArgsConstructor
@Component
public class AladinApiUtil {

    @Value("${aladin.key}")
    private String TTBKey;

    public static final String ALADIN_BASEL_URL = "http://www.aladin.co.kr/ttb/api";
    private final String ALADIN_ITEM_SEARCH_PATH = "/ItemSearch.aspx";   //상품 검색 API (책 검색용)
    private final String ALADIN_ITEM_LOOKUP_PATH = "/ItemLookUp.aspx";
    private final String ALADIN_ITEM_LIST_PATH = "/ItemList.aspx";   //상품 리스트 API (베스트셀러 조회용)

    private final int MAX_RESULTS = 10; //최대 책 검색 결과 개수
    private final String OUTPUT = "js"; // 응답 포맷 (xml 또는 json)

    //Big : 큰 크기 : 너비 200px
    //MidBig : 중간 큰 크기 : 너비 150px
    //Mid(기본값) : 중간 크기 : 너비 85px
    //Small : 작은 크기 : 너비 75px
    //Mini : 매우 작은 크기 : 너비 65px
    //None : 없음
    private final String COVER_SIZE = "MidBig";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;


    public String buildSearchApiUrl(GetBookListRequest request) {
        return String.format(
                ALADIN_BASEL_URL + ALADIN_ITEM_SEARCH_PATH +
                        "?ttbkey=%s&Query=%s&QueryType=%s&start=%d&MaxResults=%d&output=%s&CategoryId=%d&Cover=%s",
                TTBKey,
                request.getSearchTerm(),
                request.getQueryType(),
                request.getPage(),
                MAX_RESULTS,
                OUTPUT,
                request.getGenreType().getCategoryId(),
                COVER_SIZE
        );
    }

    public String buildLookUpApiUrl(String isbn) {
        return String.format(
                ALADIN_BASEL_URL + ALADIN_ITEM_LOOKUP_PATH +
                        "?ttbkey=%s&itemIdType=%s&ItemId=%s&output=%s&Cover=%s",
                TTBKey,
                isbn.length() == 13 ? "ISBN13" : "ISBN",
                isbn,
                OUTPUT,
                COVER_SIZE
        );
    }

    //알라딘 API로부터 받은 Response에 에러가 없는지 확인
    public void checkValidatedResponse(String currentResponse) {
        try {
            JsonNode root = objectMapper.readTree(currentResponse);
            if (root.has("errorCode")) {
                throw new GlobalException(ALADIN_API_ERROR);
            }
        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
        }
    }

    public String requestBookInfoFromAladinApi(String requestUrl) {
        try {
            String currentResponse = restTemplate.getForEntity(requestUrl, String.class).getBody();
            this.checkValidatedResponse(currentResponse);
            log.info("알라딘 API 응답 Body: {}", currentResponse);

            return currentResponse;
        } catch (RestClientException e) {
            throw new GlobalException(ALADIN_API_ERROR);
        }
    }

    public Book parseAladinApiResponseToBook(String currentResponse) {
        try {
            //JSON 형식 오류 허용 -> "Unrecognized character escape ''' (code 39)" 에러 해결용
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//            currentResponse = currentResponse.replace("'", "\"");

            JsonNode root = objectMapper.readTree(currentResponse);
//            GenreType genreType = GenreType.fromCategoryId(root.get("searchCategoryId").asInt());
            JsonNode items = root.get("item");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    String title = item.get("title").asText();
                    String author = item.get("author").asText();

//                    isbn 13자리가 비어있는 경우 10자리 사용
                    String isbn = item.has("isbn13") && !item.get("isbn13").asText().isEmpty()
                            ? item.get("isbn13").asText()
                            : item.get("isbn").asText();
                    String imageUrl = item.get("cover").asText();

//                    String link = item.get("link").asText();
                    String description = item.get("description").asText();
                    String categoryName = item.get("categoryName").asText();
                    String publisher = item.get("publisher").asText();
                    String publishedDate = item.get("pubDate").asText();

                    //전체 페이지 수 파싱
                    Integer pageCount = item.has("bookinfo") && item.get("bookinfo").has("itemPage")
                            ? item.get("bookinfo").get("itemPage").asInt() : null;

                    return Book.builder()
                            .isbn(isbn)
                            .bookTitle(title)
                            .authorName(author)
                            .genre(GenreType.parsingGenreType(categoryName))
                            .publisher(publisher)
                            .publishedDate(LocalDate.parse(publishedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .description(description)
                            .imageUrl(imageUrl)
                            .bestSeller(false)
                            .pageCount(pageCount)
                            .build();
                }
            }
        } catch (JsonProcessingException e) {
            log.info("Json 파싱 에러 메시지: {}", e.getMessage());
            throw new GlobalException(ALADIN_API_PARSING_ERROR);
        }
        throw new GlobalException(ALADIN_API_ERROR);
    }
}
