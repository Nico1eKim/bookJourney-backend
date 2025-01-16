package com.example.bookjourneybackend.global.response;

import com.example.bookjourneybackend.global.response.status.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonPropertyOrder({"code", "status", "message"})
public class BaseErrorResponse implements ResponseStatus {
    private final int code;
    private final HttpStatus status;
    private final String message;

    public BaseErrorResponse(HttpStatus status, String message) {
        this.code = status.value();
        this.status = status;
        this.message = message;
    }

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
