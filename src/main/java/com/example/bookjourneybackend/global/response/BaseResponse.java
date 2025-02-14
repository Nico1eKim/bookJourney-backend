package com.example.bookjourneybackend.global.response;

import com.example.bookjourneybackend.global.response.status.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonPropertyOrder({"code", "status", "message", "data"})
public class BaseResponse<T> implements ResponseStatus {

    private final int code;
    private final HttpStatus status;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    public BaseResponse(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public static <T> BaseResponse<T> of(HttpStatus status, String message, T data) {
        return new BaseResponse<>(status, message, data);
    }

    public static <T> BaseResponse<T> of(HttpStatus status, T data) {
        return of(status, status.name(), data);
    }

    public static <T> BaseResponse<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }

    public static BaseResponse<Void> of(HttpStatus status) {
        return new BaseResponse<>(status, status.name(), null);
    }

    public static BaseResponse<Void> ok() {
        return of(HttpStatus.OK);
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

