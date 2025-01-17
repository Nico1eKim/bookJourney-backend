package com.example.bookjourneybackend.global.handler;

import com.example.bookjourneybackend.global.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
        // 첫 번째 유효성 검사 실패 메시지만 가져오기
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation failed");

        log.error(errorMessage);
        return new BaseErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public BaseErrorResponse handleGeneralException(final Exception e) {
        log.error(e.getMessage());
        return new BaseErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
