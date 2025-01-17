package com.example.bookjourneybackend.domain.test;

import com.example.bookjourneybackend.global.common.exception.GlobalException;
import com.example.bookjourneybackend.global.common.response.BaseResponse;
import com.example.bookjourneybackend.global.common.response.status.BaseExceptionResponseStatus;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.bookjourneybackend.global.common.response.status.BaseExceptionResponseStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class TestController {

    // 잘못된 요청 예외를 발생시키는 엔드포인트
    @GetMapping("/badRequest")
    public String triggerBadRequest() {
        throw new GlobalException(FAILURE);
    }

    // 요청한 API가 없음 예외를 발생시키는 엔드포인트
    @GetMapping("/notFound")
    public String triggerNotFound() {
        throw new GlobalException(NOT_FOUND_API);
    }

    // API 예외를 발생시키는 엔드포인트
    @GetMapping("/runtimeException")
    public String triggerRuntimeException() {
        throw new GlobalException(CANNOT_FOUND_USER);
    }

}
