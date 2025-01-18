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
    CANNOT_FOUND_USER(5000,BAD_REQUEST, "해당하는 유저가 없습니다."),

    /**
     * 6000 : book 관련
     */
    CANNOT_FOUND_BOOK(6000, BAD_REQUEST, "책을 찾을 수 없습니다."),

    /**
     * 7000 : auth 관련
     */
    ALREADY_EXIST_EMAIL(7001, BAD_REQUEST, "이미 존재하는 이메일입니다."),
    CANNOT_FOUND_EMAIL(7002,BAD_REQUEST, "이메일이 존재하지 않습니다."),
    INVALID_PASSWORD(7003,BAD_REQUEST, "비밀번호가 일치하지 않습니다.");

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
