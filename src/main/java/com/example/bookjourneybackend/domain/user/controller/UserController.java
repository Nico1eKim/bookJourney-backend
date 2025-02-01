package com.example.bookjourneybackend.domain.user.controller;

import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersEmailRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersNicknameValidationRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersSignUpRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersVerificationEmailRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.response.PostUsersValidationResponse;
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
    public BaseResponse<PostUsersSignUpResponse> signup(@Valid @RequestBody final PostUsersSignUpRequest postUsersSignUpRequest
            , HttpServletRequest request, HttpServletResponse response) {
        log.info("[UserController.signUp]");
        return BaseResponse.ok(userService.signup(postUsersSignUpRequest, request,response));
    }

    //닉네임 중복검증
    @PostMapping("/nickname")
    public BaseResponse<PostUsersValidationResponse> validateNickname(@Valid @RequestBody final PostUsersNicknameValidationRequest postUsersNicknameValidationRequest) {
        log.info("[UserController.validateNickname]");
        return BaseResponse.ok(userService.validateNickname(postUsersNicknameValidationRequest));
    }

    //이메일 인증코드 요청
    @PostMapping("/emails/vertifications-requests")
    public BaseResponse<Void> sendMessage(@Valid @RequestBody final PostUsersEmailRequest postUsersEmailRequest)
    {   log.info("[UserController.requestEmail]");
        return BaseResponse.ok(userService.sendCodeToEmail(postUsersEmailRequest));
    }

    //이메일 인증확인 요청
    @PostMapping("/emails/vertifications")
    public  BaseResponse<PostUsersValidationResponse> verificationEmail(@Valid @RequestBody final PostUsersVerificationEmailRequest postUsersVerificationEmailRequest) {
        log.info("[UserController.verificationEmail]");
        return BaseResponse.ok(userService.verifiedCode(postUsersVerificationEmailRequest));
    }

}
