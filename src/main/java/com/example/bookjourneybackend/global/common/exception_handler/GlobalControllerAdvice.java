package com.example.bookjourneybackend.global.common.exception_handler;

import com.example.bookjourneybackend.global.common.exception.GlobalException;
import com.example.bookjourneybackend.global.common.response.BaseErrorResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.hibernate.TypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class GlobalControllerAdvice {

    //잘못된 요청
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, TypeMismatchException.class})
    public BaseErrorResponse handleGeneralException(GlobalException e) {
        log.error("[handle_GeneralException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }

    //요청한 API가 없음
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public BaseErrorResponse handleNoHandlerFoundException(GlobalException e) {
        log.error("[handle_NoHandlerFoundException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }

    //API 예외
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public BaseErrorResponse handleRestApiException(GlobalException e) {
        log.error("[handle_RestApiException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }

}
