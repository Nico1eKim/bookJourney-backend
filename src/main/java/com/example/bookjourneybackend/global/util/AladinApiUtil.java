package com.example.bookjourneybackend.global.util;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
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

    @Getter
    private final int BESTSELLER_MAX_RESULTS = 10; //베스트셀러 최대 책 검색 결과 개수
    private final int SEARCH_MAX_RESULTS = 10; //최대 책 검색 결과 개수
    private final String OUTPUT = "js"; // 응답 포맷 (xml 또는 json)
    private final int VERSION = 20131101;
    private final String BESTSELLER = "Bestseller";

    //Big : 큰 크기 : 너비 200px
    //MidBig : 중간 큰 크기 : 너비 150px
    //Mid(기본값) : 중간 크기 : 너비 85px
    //Small : 작은 크기 : 너비 75px
    //Mini : 매우 작은 크기 : 너비 65px
    //None : 없음
    private final String COVER_SIZE = "MidBig";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final DateUtil dateUtil;


    public String buildSearchApiUrl(GetBookListRequest request) {
        return String.format(
                ALADIN_BASEL_URL + ALADIN_ITEM_SEARCH_PATH +
                        "?ttbkey=%s&Query=%s&QueryType=%s&start=%d&MaxResults=%d&output=%s&CategoryId=%d&Cover=%s",
                TTBKey,
                request.getSearchTerm(),
                request.getQueryType(),
                request.getPage(),
                SEARCH_MAX_RESULTS,
                OUTPUT,
                request.getGenreType() == null ? null : request.getGenreType().getCategoryId(),
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

    public String buildSearchListApiUrl(int categoryId) {

        return String.format(
                ALADIN_BASEL_URL + ALADIN_ITEM_LIST_PATH +
                        "?ttbkey=%s&QueryType=%s&MaxResults=%d&output=%s&CategoryId=%d&Cover=%s&Version=%d",
                TTBKey,
                BESTSELLER,
                BESTSELLER_MAX_RESULTS,
                OUTPUT,
                categoryId,
                COVER_SIZE,
                VERSION
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

    public Book parseAladinApiResponseToBook(String currentResponse, boolean isBestseller,int nthItem) {
        try {
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

            JsonNode root = objectMapper.readTree(currentResponse);
            JsonNode items = root.get("item");

            if (items != null && items.isArray()) {
                JsonNode selectedItem;

                selectedItem = items.get(nthItem);

                String title = selectedItem.get("title").asText();
                String author = selectedItem.get("author").asText();

//              isbn 13자리가 비어있는 경우 10자리 사용
                String isbn = selectedItem.has("isbn13") && !selectedItem.get("isbn13").asText().isEmpty()
                        ? selectedItem.get("isbn13").asText()
                        : selectedItem.get("isbn").asText();
                String imageUrl = selectedItem.get("cover").asText();

                String description = selectedItem.get("description").asText();
                String publisher = selectedItem.get("publisher").asText();
                String publishedDate = selectedItem.get("pubDate").asText();

                //전체 페이지 수 파싱
                Integer pageCount = selectedItem.has("bookinfo") && selectedItem.get("bookinfo").has("itemPage")
                        ? selectedItem.get("bookinfo").get("itemPage").asInt() : null;

                String categoryName = isBestseller ? root.get("searchCategoryName").asText() : selectedItem.get("categoryName").asText();

                return Book.builder()
                        .isbn(isbn)
                        .bookTitle(title)
                        .authorName(author)
                        .genre(GenreType.parsingGenreType(categoryName))
                        .publisher(publisher)
                        .publishedDate(dateUtil.parseDateToLocalDateFromPublishedDateString(publishedDate))
                        .description(description)
                        .imageUrl(imageUrl)
                        .bestSeller(isBestseller)
                        .pageCount(pageCount)
                        .build();
            }
        } catch (JsonProcessingException e) {
            log.info("Json 파싱 에러 메시지: {}", e.getMessage());
            throw new GlobalException(ALADIN_API_PARSING_ERROR);
        }
        throw new GlobalException(ALADIN_API_ERROR);
    }

    public String getIsbnFromBestSellerResponse(String currentResponse,int nthItem) {

        try {
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

            JsonNode root = objectMapper.readTree(currentResponse);
            JsonNode items = root.get("item");

            if (items != null && items.isArray()) {
                JsonNode firstItem = items.get(nthItem); // 해당 아이템 가져오기
                String isbn = firstItem.has("isbn13") && !firstItem.get("isbn13").asText().isEmpty()
                        ? firstItem.get("isbn13").asText()
                        : firstItem.get("isbn").asText();

                return isbn;
            }
        } catch (JsonProcessingException e) {
            log.info("Json 파싱 에러 메시지: {}", e.getMessage());
            throw new GlobalException(ALADIN_API_PARSING_ERROR);
        }
        throw new GlobalException(ALADIN_API_ERROR);
    }


}
