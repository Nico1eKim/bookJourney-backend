package com.example.bookjourneybackend.domain.auth.controller;

import com.example.bookjourneybackend.domain.auth.domain.dto.request.PostAuthAccessTokenReissueRequest;
import com.example.bookjourneybackend.domain.auth.domain.dto.request.PostAuthLoginRequest;
import com.example.bookjourneybackend.domain.auth.domain.dto.response.PostAuthAccessTokenReissueResponse;
import com.example.bookjourneybackend.domain.auth.domain.dto.response.PostAuthLoginResponse;
import com.example.bookjourneybackend.domain.auth.service.AuthService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     *  로그인
     * @param authLoginRequest
     * @return PostAuthLoginResponse
     */
    @PostMapping("/login")
    public BaseResponse<PostAuthLoginResponse> login(@RequestBody final PostAuthLoginRequest authLoginRequest
            ,HttpServletResponse response) {
        log.info("[AuthController.login]");
        return BaseResponse.ok(authService.login(authLoginRequest, response));
    }

    //엑세스 토큰 재발급
    @PostMapping("/reissue")
    public BaseResponse<PostAuthAccessTokenReissueResponse> tokenReissue(@RequestBody final PostAuthAccessTokenReissueRequest
                                                                                     authAccessTokenReissueRequest,
                                                                         HttpServletResponse response, HttpServletRequest request) {
        log.info("[AuthController.tokenReissue]");
        return BaseResponse.ok(authService.tokenReissue(authAccessTokenReissueRequest,response,request));
    }


}
