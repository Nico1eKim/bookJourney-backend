package com.example.bookjourneybackend.domain.user.controller;

import com.example.bookjourneybackend.domain.user.dto.response.GetMyPageCalendarResponse;
import com.example.bookjourneybackend.domain.user.service.MyPageService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/calendar")
    public BaseResponse<GetMyPageCalendarResponse> getMyPageCalendar(
            @LoginUserId final Long userId,
            @RequestParam final Integer month,
            @RequestParam final Integer year) {
        return BaseResponse.ok(myPageService.showMyPageCalendar(userId, month, year));
    }

    @GetMapping("/calendar/info")
    public BaseResponse<GetMyPageCalendarResponse> getMyPageCalendarInfo(
            @LoginUserId final Long userId,
            @RequestParam final Integer month,
            @RequestParam final Integer year,
            @RequestParam final Integer day) {
        return BaseResponse.ok(myPageService.showMyPageCalendarInfo(userId, month, year, day));
    }

}
