package com.example.bookjourneybackend.global.response.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ResponseStatus{

    NO_SUCH_USER(1000, HttpStatus.BAD_REQUEST, "알맞은 유저를 찾을 수 없습니다..");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
