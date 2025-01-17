package com.example.bookjourneybackend.global.common.exception;

import com.example.bookjourneybackend.global.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException{

    private final ResponseStatus exceptionStatus;

    public GlobalException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
