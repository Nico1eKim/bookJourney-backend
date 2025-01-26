package com.example.bookjourneybackend.global.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@RequiredArgsConstructor
public enum BaseExceptionResponseStatus implements ResponseStatus{

    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(2000,OK, "요청에 성공하였습니다."),
    FAILURE(4000, BAD_REQUEST, "요청에 실패하였습니다."),
    NOT_FOUND_API(4040,NOT_FOUND,"존재하지 않는 API입니다."),

    /**
     * 5000 : user 관련
     */
    CANNOT_FOUND_USER(5000,BAD_REQUEST, "유저를 찾을 수 없습니다."),

    /**
     * 6000 : book 관련
     */
    CANNOT_FOUND_BOOK(6000, BAD_REQUEST, "책을 찾을 수 없습니다."),

    INVALID_GENRE(6001, BAD_REQUEST, "알맞은 장르를 찾을 수 없습니다"),
    EMPTY_SEARCH_TERM(6001, BAD_REQUEST, "검색어는 비워둘 수 없습니다."),
    INVALID_PAGE(6001, BAD_REQUEST, "페이지 번호는 1 이상입니다."),
    INVALID_QUERY_TYPE(6001, BAD_REQUEST, "알맞은 검색 종류가 아닙니다."),

    ALADIN_API_ERROR(6002, BAD_REQUEST, "알라딘 API 호출에 실패하였습니다."),
    ALADIN_API_PARSING_ERROR(6003, BAD_REQUEST, "알라딘 API 응답 파싱에 실패하였습니다."),

    CANNOT_FOUND_POPULAR_BOOK(6004, BAD_REQUEST, "읽기횟수가 가장 많은 책을 찾을 수 없습니다."),



    /**
     * 7000 : auth 관련
     */
    ALREADY_EXIST_EMAIL(7001, BAD_REQUEST, "이미 존재하는 이메일입니다."),
    CANNOT_FOUND_EMAIL(7002,BAD_REQUEST, "이메일이 존재하지 않습니다."),
    INVALID_PASSWORD(7003,BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    /**
     * 8000 : room 관련
     */
    CANNOT_FIND_ROOM(8001 ,BAD_REQUEST, "방을 찾을 수 없습니다."),
    INVALID_ROOM_TYPE(8002, BAD_REQUEST, "알맞은 방 타입을 찾을 수 없습니다."),
    INVALID_SEARCH_TYPE(8003, BAD_REQUEST, "알맞은 검색 조건을 찾을 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
