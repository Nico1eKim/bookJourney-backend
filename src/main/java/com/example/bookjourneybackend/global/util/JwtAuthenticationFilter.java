package com.example.bookjourneybackend.global.util;

import com.example.bookjourneybackend.domain.user.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


@Order(0)
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * JWT 토큰 검증 필터 수행
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // request 헤더에서 액세스 토큰 추출
            String accessToken = jwtUtil.resolveAccessToken(request);

            if(accessToken != null) {

                // 엑세스 토큰이 유효한 상황
                if (jwtUtil.validateToken(accessToken)) {
                    // Authorization 헤더에서 User ID 추출
                    Long userId = jwtUtil.extractUserIdFromJwtToken(accessToken);
                    setAuthentication(request, userId);
                }
            }
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }
        filterChain.doFilter(request, response);

    }


    // SecurityContext 에 Authentication 객체 저장
    public void setAuthentication(HttpServletRequest request,Long userId) {

        // User 객체 생성
        User user = User.builder().userId(userId).build();

        // 인증된 사용자 객체 생성
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        AbstractAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(user, null, authorities);
        authenticated.setDetails(new WebAuthenticationDetails(request));

        // SecurityContext에 Authentication 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authenticated);

    }


}
