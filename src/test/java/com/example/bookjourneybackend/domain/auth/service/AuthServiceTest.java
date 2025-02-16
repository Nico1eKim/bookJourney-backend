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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.bookjourneybackend.global.entity.EntityStatus.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RedisService redisService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private HttpServletResponse response;
    private HttpServletRequest request;

    String email = "test@example.com";
    String password = "password123";
    String nickName = "testUser";
    Long userId = 1L;

    @Test
    @DisplayName("로그인_성공")
    void 로그인_성공() {

        // given
        User mockUser = new User(userId, email, passwordEncoder.encode(password), nickName);

        PostAuthLoginRequest loginRequest = new PostAuthLoginRequest(email, password);

        when(userRepository.findByEmailAndStatus(email,ACTIVE))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword()))
                .thenReturn(true);
        when(jwtUtil.createAccessToken(mockUser.getUserId()))
                .thenReturn("mockAccessToken");
        when(jwtUtil.createRefreshToken(mockUser.getUserId()))
                .thenReturn("mockRefreshToken");

        // when
        PostAuthLoginResponse postAuthLoginResponse = authService.login(loginRequest, request, response);

        // then
        assertNotNull(postAuthLoginResponse);
        assertEquals("mockAccessToken", postAuthLoginResponse.getAccessToken());
        assertEquals("mockRefreshToken", postAuthLoginResponse.getRefreshToken());

        doNothing().when(jwtAuthenticationFilter).setAuthentication(request, mockUser.getUserId());

        verify(redisService).storeRefreshToken("mockRefreshToken", mockUser.getUserId());
    }

    @Test
    @DisplayName("로그인_실패_이메일_없음")
    void 로그인_실패_이메일_없음() {

        // given
        PostAuthLoginRequest loginRequest = new PostAuthLoginRequest(email, password);

        when(userRepository.findByEmailAndStatus(email, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(GlobalException.class, () -> authService.login(loginRequest, request, response));
    }

    @Test
    @DisplayName("로그인_실패_비밀번호_불일치")
    void 로그인_실패_비밀번호_불일치() {

        // given
        User mockUser = new User(userId, email, passwordEncoder.encode("correctPassword"), nickName);

        PostAuthLoginRequest loginRequest = new PostAuthLoginRequest(email, password);

        when(userRepository.findByEmailAndStatus(email, ACTIVE))
                .thenReturn(Optional.of(mockUser));

        when(passwordEncoder.matches(password, mockUser.getPassword()))
                .thenReturn(false);

        // when & then
        assertThrows(GlobalException.class, () -> authService.login(loginRequest, request, response));
    }

    @Test
    @DisplayName("엑세스_토큰_재발급_성공")
    void 엑세스_토큰_재발급_성공() {

        // given
        User mockUser = new User(userId, email, passwordEncoder.encode(password), nickName);
        String oldRefreshToken = "validRefreshToken";
        String newAccessToken = "newAccessToken";

        PostAuthAccessTokenReissueRequest postAuthAccessTokenReissueRequest = new PostAuthAccessTokenReissueRequest(oldRefreshToken);

        when(jwtUtil.validateToken(oldRefreshToken)).thenReturn(true);
        when(jwtUtil.extractUserIdFromJwtToken(oldRefreshToken)).thenReturn(mockUser.getUserId());
        when(redisService.checkTokenExists(mockUser.getUserId().toString())).thenReturn(true);
        when(jwtUtil.createAccessToken(mockUser.getUserId())).thenReturn(newAccessToken);

        // when
        PostAuthAccessTokenReissueResponse postAuthAccessTokenReissueResponse = authService.tokenReissue(postAuthAccessTokenReissueRequest, this.response, this.request);

        // then
        assertNotNull(postAuthAccessTokenReissueResponse);
        assertEquals(newAccessToken, postAuthAccessTokenReissueResponse.getAccessToken());
        verify(jwtAuthenticationFilter).setAuthentication(request, userId);

    }

    @Test
    @DisplayName("엑세스_토큰_재발급_실패_리프레시_토큰_만료")
    void 엑세스_토큰_재발급_실패_리프레시_토큰_만료() {

        // given
        String expiredRefreshToken = "expiredToken";
        PostAuthAccessTokenReissueRequest postAuthAccessTokenReissueRequest
                = new PostAuthAccessTokenReissueRequest(expiredRefreshToken);

        when(jwtUtil.validateToken(expiredRefreshToken)).thenReturn(false);

        // when & then
        assertThrows(GlobalException.class, () -> authService.tokenReissue(postAuthAccessTokenReissueRequest, response, this.request));
    }

    @Test
    @DisplayName("로그아웃_성공")
    void 로그아웃_성공() {

        // given
        User mockUser = new User(userId, email, passwordEncoder.encode(password), nickName);
        when(userRepository.findByUserIdAndStatus(userId, ACTIVE))
                .thenReturn(Optional.of(mockUser));

        // when
        authService.logout(userId);

        // then
        verify(redisService).invalidateToken(mockUser.getUserId());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

