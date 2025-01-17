package com.example.bookjourneybackend.global.common.response.status;

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
    CANNOT_FOUND_USER(3000,BAD_REQUEST, "유저를 찾을 수 없습니다."),
    ALREADY_EXIST_EMAIL(3001, BAD_REQUEST, "이미 존재하는 이메일입니다."),

    /**
     * 6000 : book 관련
     */
    CANNOT_FOUND_BOOK(6000, BAD_REQUEST, "책을 찾을 수 없습니다.");

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
