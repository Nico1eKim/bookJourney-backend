package com.example.bookjourneybackend.domain.auth.service;

import com.example.bookjourneybackend.domain.auth.domain.dto.request.PostAuthAccessTokenReissueRequest;
import com.example.bookjourneybackend.domain.auth.domain.dto.request.PostAuthLoginRequest;
import com.example.bookjourneybackend.domain.auth.domain.dto.response.PostAuthAccessTokenReissueResponse;
import com.example.bookjourneybackend.domain.auth.domain.dto.response.PostAuthLoginResponse;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.JwtAuthenticationFilter;
import com.example.bookjourneybackend.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;

import static com.example.bookjourneybackend.global.entity.EntityStatus.ACTIVE;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Transactional
    public PostAuthLoginResponse login(PostAuthLoginRequest authLoginRequest, HttpServletResponse response) {
        log.info("[AuthService.login]");

        String email = authLoginRequest.getEmail();
        String password = authLoginRequest.getPassword();

        User user = userRepository.findByEmailAndStatus(email,ACTIVE)
                .orElseThrow(()-> new GlobalException(CANNOT_FOUND_EMAIL));

        // 암호화된 password를 디코딩한 값과 입력한 패스워드 값이 다르면 null 반환
        // 회원가입시 암호화된 비밀번호 저장하는것으로 리펙토링
        //if(!passwordEncoder.matches(password,user.getPassword()))
        if(!user.getPassword().equals(password)) {
            log.info(password);
            log.info(user.getPassword());
            throw new GlobalException(INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());
        jwtUtil.setHeaderAccessToken(response,accessToken);
        jwtUtil.setHeaderRefreshToken(response,refreshToken);
        tokenService.storeRefreshToken(refreshToken, user.getUserId());

        // 인증 정보 SecurityContext에 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return  PostAuthLoginResponse.of(user.getUserId(),accessToken,refreshToken);
    }


    @Transactional
    public PostAuthAccessTokenReissueResponse tokenReissue(PostAuthAccessTokenReissueRequest authAccessTokenReissueRequest,
                                                           HttpServletResponse response, HttpServletRequest request) {
        log.info("[AuthService.tokenReissue]");

        String refreshToken = authAccessTokenReissueRequest.getRefreshToken();

        //리프레쉬 토큰이 존재하지 않는 경우
        if (refreshToken == null)
            throw new GlobalException(NOT_EXIST_TOKEN);

        // 리프레시 토큰 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new GlobalException(INVALID_TOKEN);
        }

        /// 리프레시 토큰으로 유저 정보 가져오기
        Long userId = jwtUtil.extractUserIdFromJwtToken(refreshToken);

        /// 리프레시 토큰 저장소 존재유무 확인
        boolean isRefreshToken = tokenService.checkTokenExists(userId.toString());
        if (isRefreshToken) {

            /// 토큰 재발급
            String newAccessToken = jwtUtil.createAccessToken(userId);
            /// 헤더에 엑세스 토큰 추가
            jwtUtil.setHeaderAccessToken(response, newAccessToken);

            jwtAuthenticationFilter.setAuthentication(request,userId);

            return PostAuthAccessTokenReissueResponse.of(newAccessToken);

        } else throw new GlobalException(NOT_EXIST_TOKEN);

    }


}
