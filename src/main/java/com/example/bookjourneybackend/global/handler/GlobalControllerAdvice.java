package com.example.bookjourneybackend.global.handler;

import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.response.BaseErrorResponse;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    @ExceptionHandler({GlobalException.class, RuntimeException.class})
    public BaseErrorResponse handleRestApiException(GlobalException e) {
        log.error("[handle_RestApiException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }

    //dto 유효성 검증 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        // 첫 번째 유효성 검사 실패 메시지만 가져오기
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation failed");

        return BaseResponse.of(HttpStatus.BAD_REQUEST, errorMessage, null);
    }
}
