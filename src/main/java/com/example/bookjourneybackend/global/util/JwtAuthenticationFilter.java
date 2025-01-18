package com.example.bookjourneybackend.global.util;

import com.example.bookjourneybackend.domain.auth.service.TokenService;
import com.example.bookjourneybackend.domain.user.domain.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Order(0)
@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    /**
     * JWT 토큰 검증 필터 수행
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 액세스 토큰 추출
        String accessToken = parseBearerToken(request, "Authorization");
        // Refresh-Token 헤더에서 리프레시 토큰 추출
        String refreshToken = request.getHeader("Refresh-Token");

        // 액세스 토큰이 존재하는 경우
        if (accessToken != null) {
            try {
                log.info("Access Token: {}", accessToken);

                // 액세스 토큰 검증
                if (jwtUtil.validateAccessToken(accessToken)) {
                    String userId = String.valueOf(jwtUtil.parseClaims(accessToken).get("userId"));

                    if (tokenService.checkTokenExists(userId)) { // 리프레시 토큰이 존재하는지 확인
                        User user = User.builder().userId(Long.valueOf(userId)).build();
                        AbstractAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(user, null, null);
                        authenticated.setDetails(new WebAuthenticationDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticated);
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,  "리프레시 토큰이 일치하지 않습니다.");
                        return;
                    }
                }
            }
            catch (ExpiredJwtException e) {
                log.warn("Expired Access Token: {}", accessToken);

                // 액세스 토큰이 만료되었을 경우 리프레시 토큰 검증 및 새로운 액세스 토큰 발급

                if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken)) {
                    String userId = String.valueOf(jwtUtil.parseClaims(refreshToken).get("userId"));

                    if (tokenService.checkTokenExists(userId)) {

                        User user = User.builder().userId(Long.valueOf(userId)).build();
                        AbstractAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(user, null, null);
                        authenticated.setDetails(new WebAuthenticationDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticated);

                        // 새로운 액세스 토큰 발급
                        String newAccessToken = jwtUtil.createAccessToken(Long.valueOf(userId));

                        // 액세스 토큰을 response 헤더에 추가 (혹은 body에 포함시켜 응답)
                        response.setHeader("Access-Token", newAccessToken);

                        // 토큰 발급 후 종료
                        return;
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다.");
                        return;
                    }

                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "리프레시 토큰이 유효하지 않거나 존재하지 않습니다.");
                    return;
                }
            }
            catch (Exception e) {
                log.error("Error validating access token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증되지 않은 요청입니다.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request, String header) {
        return Optional.ofNullable(request.getHeader(header))
                .filter(token -> token.startsWith("Bearer "))
                .map(token -> token.substring(7).trim()) // 공백 제거
                .orElse(null);
    }

}
