package com.example.bookjourneybackend.domain.auth.service;

import com.example.bookjourneybackend.domain.auth.domain.dto.request.PostAuthLoginRequest;
import com.example.bookjourneybackend.domain.auth.domain.dto.response.PostAuthLoginResponse;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.util.JwtAuthenticationFilter;
import com.example.bookjourneybackend.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.bookjourneybackend.global.entity.EntityStatus.ACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisService redisService;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Test
    @DisplayName("로그인_성공")
    void 로그인_성공() {
        // given (테스트에 필요한 데이터 준비)
        String email = "test@example.com";
        String password = "password123";
        String nickName = "testUser";
        User mockUser = new User(1L, email, passwordEncoder.encode(password), nickName);

        PostAuthLoginRequest loginRequest = new PostAuthLoginRequest(email, password);

        when(userRepository.findByEmailAndStatus(email,ACTIVE))
                .thenReturn(Optional.of(mockUser));

        when(passwordEncoder.matches(password, mockUser.getPassword()))
                .thenReturn(true);

        when(jwtUtil.createAccessToken(mockUser.getUserId()))
                .thenReturn("mockAccessToken");

        when(jwtUtil.createRefreshToken(mockUser.getUserId()))
                .thenReturn("mockRefreshToken");

        // when (테스트할 메서드 실행)
        PostAuthLoginResponse response = authService.login(loginRequest, request, this.response);

        // then (검증)
        assertNotNull(response);
        assertEquals("mockAccessToken", response.getAccessToken());
        assertEquals("mockRefreshToken", response.getRefreshToken());

        verify(jwtAuthenticationFilter).setAuthentication(request, mockUser.getUserId());
        verify(redisService).storeRefreshToken("mockRefreshToken", mockUser.getUserId());
    }

//    @Test
//    void 로그인_실패_이메일_없음() {
//        // given
//        String email = "nonexistent@example.com";
//        String password = "password123";
//        PostAuthLoginRequest loginRequest = new PostAuthLoginRequest(email, password);
//
//        when(userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE))
//                .thenReturn(Optional.empty());
//
//        // when & then (예외 발생 검증)
//        assertThrows(GlobalException.class, () -> authService.login(loginRequest, request, response));
//    }
//
//    @Test
//    void 로그인_실패_비밀번호_불일치() {
//        // given
//        String email = "test@example.com";
//        String password = "wrongPassword";
//        User mockUser = new User(1L, email, passwordEncoder.encode("correctPassword"), UserStatus.ACTIVE);
//
//        PostAuthLoginRequest loginRequest = new PostAuthLoginRequest(email, password);
//
//        when(userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE))
//                .thenReturn(Optional.of(mockUser));
//
//        when(passwordEncoder.matches(password, mockUser.getPassword()))
//                .thenReturn(false);
//
//        // when & then (예외 발생 검증)
//        assertThrows(GlobalException.class, () -> authService.login(loginRequest, request, response));
//    }
//
//    @Test
//    void 엑세스_토큰_재발급_성공() {
//        // given
//        Long userId = 1L;
//        String oldRefreshToken = "validRefreshToken";
//        String newAccessToken = "newAccessToken";
//
//        PostAuthAccessTokenReissueRequest request = new PostAuthAccessTokenReissueRequest(oldRefreshToken);
//
//        when(jwtUtil.validateToken(oldRefreshToken)).thenReturn(true);
//        when(jwtUtil.extractUserIdFromJwtToken(oldRefreshToken)).thenReturn(userId);
//        when(redisService.checkTokenExists(userId.toString())).thenReturn(true);
//        when(jwtUtil.createAccessToken(userId)).thenReturn(newAccessToken);
//
//        // when
//        PostAuthAccessTokenReissueResponse response = authService.tokenReissue(request, this.response, this.request);
//
//        // then
//        assertNotNull(response);
//        assertEquals(newAccessToken, response.getAccessToken());
//
//        verify(jwtAuthenticationFilter).setAuthentication(this.request, userId);
//    }
//
//    @Test
//    void 엑세스_토큰_재발급_실패_리프레시_토큰_만료() {
//        // given
//        String expiredRefreshToken = "expiredToken";
//        PostAuthAccessTokenReissueRequest request = new PostAuthAccessTokenReissueRequest(expiredRefreshToken);
//
//        when(jwtUtil.validateToken(expiredRefreshToken)).thenReturn(false);
//
//        // when & then
//        assertThrows(GlobalException.class, () -> authService.tokenReissue(request, response, this.request));
//    }
//
//    @Test
//    void 로그아웃_성공() {
//        // given
//        Long userId = 1L;
//
//        when(userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE))
//                .thenReturn(Optional.of(new User(userId, "test@example.com", "password", UserStatus.ACTIVE)));
//
//        // when
//        authService.logout(userId);
//
//        // then
//        verify(redisService).invalidateToken(userId);
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//    }
}

