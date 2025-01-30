package com.example.bookjourneybackend.domain.user.controller;

import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersSignUpRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.response.PostUsersSignUpResponse;
import com.example.bookjourneybackend.domain.user.service.UserService;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public BaseResponse<PostUsersSignUpResponse> signup(@Valid @RequestBody final PostUsersSignUpRequest userSignUpRequest
            , HttpServletRequest request, HttpServletResponse response) {
        log.info("[UserController.signUp]");
        return BaseResponse.ok(userService.signup(userSignUpRequest, request,response));
    }

    //닉네임 중복검증
    @PostMapping("/nickname")
    public BaseResponse<PostUsersSignUpResponse> validateNickname(@RequestBody final PostUsersSignUpRequest userSignUpRequest
            , HttpServletRequest request, HttpServletResponse response) {
        log.info("[UserController.validateNickname]");
        return BaseResponse.ok(userService.validateNickname());
    }
}
