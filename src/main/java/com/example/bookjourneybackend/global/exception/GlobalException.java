package com.example.bookjourneybackend.global.exception;

import com.example.bookjourneybackend.global.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException{

    private final ResponseStatus exceptionStatus;

    public GlobalException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
