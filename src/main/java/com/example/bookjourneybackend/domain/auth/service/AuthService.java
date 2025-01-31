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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bookjourneybackend.global.entity.EntityStatus.ACTIVE;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 1. 이메일로 등록된 회원인지 검사
     * 2. 비밀번호 검사로 로그인
     * 3. 토큰 발급 및 인증된 사용자 권한 설정
     * @param authLoginRequest,request,response
     * @return PostAuthLoginResponse
     */
    @Transactional
    public PostAuthLoginResponse login(PostAuthLoginRequest authLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("[AuthService.login]");

        String email = authLoginRequest.getEmail();
        String password = authLoginRequest.getPassword();

        User user = userRepository.findByEmailAndStatus(email,ACTIVE)
                .orElseThrow(()-> new GlobalException(CANNOT_FOUND_EMAIL));

        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new GlobalException(INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        jwtUtil.setHeaderAccessToken(response,accessToken);
        redisService.storeRefreshToken(refreshToken, user.getUserId());

        //인증된 사용자 권한 설정
        jwtAuthenticationFilter.setAuthentication(request,user.getUserId());

        return  PostAuthLoginResponse.of(user.getUserId(),accessToken,refreshToken);
    }


    /**
     * 1. 리프레쉬 토큰 검증
     * 2. 검증 후 리프레쉬 토큰 저장소에서 찾아옴
     * 3. 검증된 리프레쉬 토큰으로 엑세스 토큰 재발급 및 인증된 사용자 권한 설정
     * @param authAccessTokenReissueRequest,request,response
     * @return PostAuthAccessTokenReissueResponse
     */
    @Transactional(readOnly = true)
    public PostAuthAccessTokenReissueResponse tokenReissue(PostAuthAccessTokenReissueRequest authAccessTokenReissueRequest,
                                                           HttpServletResponse response, HttpServletRequest request) {
        log.info("[AuthService.tokenReissue]");

        String refreshToken = authAccessTokenReissueRequest.getRefreshToken();

        // 리프레시 토큰 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new GlobalException(EXPIRED_TOKEN);
        }

        /// 리프레시 토큰으로 유저 정보 가져오기
        Long userId = jwtUtil.extractUserIdFromJwtToken(refreshToken);

        /// 리프레시 토큰 저장소 존재유무 확인
        boolean isRefreshToken = redisService.checkTokenExists(userId.toString());
        if (isRefreshToken) {

            /// 토큰 재발급
            String newAccessToken = jwtUtil.createAccessToken(userId);
            /// 헤더에 엑세스 토큰 추가
            jwtUtil.setHeaderAccessToken(response, newAccessToken);
            //인증된 사용자 권한 설정
            jwtAuthenticationFilter.setAuthentication(request,userId);

            return PostAuthAccessTokenReissueResponse.of(newAccessToken);

        } throw new GlobalException(NOT_EXIST_TOKEN);

    }

    /**
     * 1. 로그인 된 유저인지 확인
     * 2. 로그아웃
     * 3. 토큰 무효화
     * @param userId
     */
    @Transactional
    public void logout(Long userId) {
        log.info("[AuthService.logout]");

        //해당하는 유저 찾기
        userRepository.findByUserIdAndStatus(userId, ACTIVE)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        //리프레쉬 토큰 저장소에서 삭제
        redisService.invalidateToken(userId);

        // Spring Security에서 인증 정보 초기화
        SecurityContextHolder.clearContext();  // 인증 정보 초기화

    }
}
