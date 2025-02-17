package com.example.bookjourneybackend.domain.user.controller;

import com.example.bookjourneybackend.domain.user.dto.request.PostUsersEmailRequest;
import com.example.bookjourneybackend.domain.user.dto.request.PostUsersNicknameValidationRequest;
import com.example.bookjourneybackend.domain.user.dto.request.PostUsersSignUpRequest;
import com.example.bookjourneybackend.domain.user.dto.request.PostUsersVerificationEmailRequest;
import com.example.bookjourneybackend.domain.user.dto.response.PostUsersSignUpResponse;
import com.example.bookjourneybackend.domain.user.dto.response.PostUsersValidationResponse;
import com.example.bookjourneybackend.domain.user.service.UserService;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public BaseResponse<PostUsersSignUpResponse> signup(@Valid @RequestBody final PostUsersSignUpRequest postUsersSignUpRequest
            , HttpServletRequest request, HttpServletResponse response) {
        return BaseResponse.ok(userService.signup(postUsersSignUpRequest, request,response));
    }

    //닉네임 중복검증
    @PostMapping("/nickname")
    public BaseResponse<PostUsersValidationResponse> validateNickname(@Valid @RequestBody final PostUsersNicknameValidationRequest postUsersNicknameValidationRequest) {
        return BaseResponse.ok(userService.validateNickname(postUsersNicknameValidationRequest));
    }

    //이메일 인증코드 요청
    @PostMapping("/emails/vertifications-requests")
    public BaseResponse<Void> sendMessage(@Valid @RequestBody final PostUsersEmailRequest postUsersEmailRequest)
    {
        return BaseResponse.ok(userService.sendCodeToEmail(postUsersEmailRequest));
    }

    //이메일 인증확인 요청
    @PostMapping("/emails/vertifications")
    public  BaseResponse<PostUsersValidationResponse> verificationEmail(@Valid @RequestBody final PostUsersVerificationEmailRequest postUsersVerificationEmailRequest) {
        return BaseResponse.ok(userService.verifiedCode(postUsersVerificationEmailRequest));
    }
}
