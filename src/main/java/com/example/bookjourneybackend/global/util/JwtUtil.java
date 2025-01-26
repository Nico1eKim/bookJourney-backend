package com.example.bookjourneybackend.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.example.bookjourneybackend.global.util.HttpHeader.*;

@Slf4j
@Component
public class JwtUtil {

    private final Key secretKey;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpTime,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    /**
     * Access Token 생성
     *
     * @param userId
     * @return Access Token String
     */
    public String createAccessToken(Long userId) {
        return createToken(userId, accessTokenExpTime);
    }

    /**
     * Refresh Token 생성
     *
     * @param userId
     * @return Refresh Token String
     */
    public String createRefreshToken(Long userId) {
        return createToken(userId, refreshTokenExpTime);
    }

    /**
     * JWT 생성
     *
     * @param userId
     * @param expireTime
     * @return JWT String
     */
    private String createToken(Long userId, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);

        Date now = new Date();
        Date tokenValidity = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(tokenValidity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(AUTHORIZATION.getValue(), BEARER.getValue() + accessToken);
    }

    // 리프레시 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshToken, BEARER.getValue() + refreshToken);
    }

    // Request Header에서 Bearer Token 값 추출
    private String parseBearerToken(HttpServletRequest request, String header) {
        return Optional.ofNullable(request.getHeader(header))
                .filter(token -> token.startsWith(BEARER.getValue()))
                .map(token -> token.substring(7).trim()) // "Bearer "를 제거하고 공백을 제거
                .orElse(null);
    }

    // AccessToken 추출
    public String resolveAccessToken(HttpServletRequest request) {
        return parseBearerToken(request, AUTHORIZATION.getValue());
    }

    // RefreshToken 추출
    public String resolveRefreshToken(HttpServletRequest request) {
        return parseBearerToken(request, REFRESH_TOKEN.getValue());
    }


    /**
     * Token에서 User ID 추출
     *
     * @param authorization
     * @return User ID
     */
    public Long extractIdFromHeader(String authorization) {

        // Authorization 헤더에서 JWT 토큰 추출
        String jwtToken;
        try {
            jwtToken = extractJwtToken(authorization);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 헤더 포맷입니다.");
        }

        // JWT 토큰에서 사용자 정보 추출
        Long userId;
        try {
            userId = extractUserIdFromJwtToken(jwtToken);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("토큰에서 유저 아이디를 찾을 수 없습니다.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 토큰 형식입니다.");
        }

        return userId;
    }

    private String extractJwtToken(String authorizationHeader) {
        String[] parts = authorizationHeader.split(" ");
        if (parts.length == 2) {
            return parts[1].trim(); // 토큰 부분 추출 및 공백 제거
        }
        throw new IllegalArgumentException("유효하지 않은 헤더 포맷입니다.");
    }

    public Long extractUserIdFromJwtToken(String jwtToken) {

        Claims claims = parseClaims(jwtToken);
        Long userId = claims.get("userId", Long.class);

        if (userId == null) {
            throw new NoSuchElementException("토큰에서 유저 아이디를 찾을 수 없습니다.");
        }
        return userId;
    }


    /**
     * JWT 검증
     *
     * @param token
     * @return IsValidate
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 토큰입니다.", e);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT 클레임 문자열이 비어있습니다.", e);
        }
        return false;
    }

    /**
     * JWT Claims 추출
     *
     * @param token
     * @return JWT Claims
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}