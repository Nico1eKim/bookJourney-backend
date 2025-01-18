package com.example.bookjourneybackend.domain.auth.controller;

import com.example.bookjourneybackend.domain.auth.domain.dto.request.PostAuthLoginRequest;
import com.example.bookjourneybackend.domain.auth.domain.dto.response.PostAuthLoginResponse;
import com.example.bookjourneybackend.domain.auth.service.AuthService;
import com.example.bookjourneybackend.global.response.BaseResponse;
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
    public BaseResponse<PostAuthLoginResponse> login(@Valid @RequestBody PostAuthLoginRequest authLoginRequest) {
        log.info("[AuthController.login]");
        return BaseResponse.ok(authService.login(authLoginRequest));
    }


}
