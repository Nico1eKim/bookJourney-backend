package com.example.bookjourneybackend.global.util;

import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.ALADIN_API_ERROR;

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
}
