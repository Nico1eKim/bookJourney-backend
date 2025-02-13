package com.example.bookjourneybackend.domain.user.controller;

import com.example.bookjourneybackend.domain.user.dto.request.PatchUserInfoRequest;
import com.example.bookjourneybackend.domain.user.dto.request.PatchUsersPasswordRequest;
import com.example.bookjourneybackend.domain.user.dto.response.GetMyPageCalendarResponse;
import com.example.bookjourneybackend.domain.user.dto.response.GetMyPageUserInfoResponse;
import com.example.bookjourneybackend.domain.user.dto.response.PatchUserInfoResponse;
import com.example.bookjourneybackend.domain.user.service.MyPageService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public BaseResponse<GetMyPageUserInfoResponse> getMyPageUserInfo(
            @LoginUserId final Long userId
    ) {
        return BaseResponse.ok(myPageService.showMyPageUserInfo(userId));
    }

    @PatchMapping("/profile")
    public BaseResponse<PatchUserInfoResponse> updateMyPageProfile(
            @RequestBody final PatchUserInfoRequest patchUserInfoRequest,
            @LoginUserId final Long userId){
        return BaseResponse.ok(myPageService.updateMyPageProfile(patchUserInfoRequest,userId));
    }

    @PatchMapping("/password")
    public BaseResponse<Void> updateMyPagePassword(
            @LoginUserId final Long userId,
            @Valid @RequestBody final PatchUsersPasswordRequest patchUsersPasswordRequest
    ) {
        myPageService.updateMyPagePassword(userId, patchUsersPasswordRequest);
        return BaseResponse.ok();
    }

}
