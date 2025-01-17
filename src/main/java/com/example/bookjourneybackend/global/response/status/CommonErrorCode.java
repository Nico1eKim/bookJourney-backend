package com.example.bookjourneybackend.global.response.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ResponseStatus{

    INTERNAL_SERVER_ERROR(000, HttpStatus.INTERNAL_SERVER_ERROR, "서버내부 오류입니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}


