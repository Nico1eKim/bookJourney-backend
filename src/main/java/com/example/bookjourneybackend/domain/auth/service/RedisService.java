package com.example.bookjourneybackend.domain.auth.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RedisService {

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Getter
    @Value("${spring.mail.auth-code-expiration-minutes}")
    private long authCodeExpirationMinutes;

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String AUTH_CODE_PREFIX = "AuthCode:";
    private static final String AUTH_REQUEST_PREFIX = "AuthRequest:";

    // redis 에 토큰 저장
    public void storeRefreshToken(String token, Long userId) {
        log.info("[RedisService.storeRefreshToken]");
        redisTemplate.opsForValue().set(String.valueOf(userId), token, refreshTokenExpiration, MILLISECONDS);
    }

    // 주어진 userId로 저장된 토큰이 Redis에 존재하는지 확인
    public boolean checkTokenExists(String userId) {
        log.info("[RedisService.checkTokenExists]");
        Boolean result = redisTemplate.hasKey(userId);
        return result != null && result;
    }

    // redis 에서 토큰 삭제
    public void invalidateToken(Long userId) {
        log.info("[RedisService.invalidateToken]");
        redisTemplate.delete(String.valueOf(userId));
    }

    // 이메일 인증 요청 시 인증 번호 Redis 에 저장 ( key = "AuthCode:" + Email / value = AuthCode )
    // 인증 요청 여부도 함께 저장 ( key = "AuthRequest:" + Email  / value = true )
    public void storeAuthCode(String email, String authCode) {
        log.info("[RedisService.storeAuthCode]");
        // 인증 코드 저장
        redisTemplate.opsForValue().set(AUTH_CODE_PREFIX + email, authCode, authCodeExpirationMinutes,MINUTES);
        // 인증 요청 여부 저장
        redisTemplate.opsForValue().set(AUTH_REQUEST_PREFIX + email, "true", authCodeExpirationMinutes * 2,MINUTES);
    }

    // 주어진 이메일로 인증 번호 조회
    public Optional<String> getAuthCode(String email) {
        log.info("[RedisService.getAuthCode]");
        Object value = redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + email);
        return Optional.ofNullable(value).map(Object::toString);  // value가 null이면 Optional.empty() 반환
    }

    // redis 에서 인증 번호 삭제
    public void deleteAuthCode(String email) {
        log.info("[RedisService.deleteAuthCode]");
        redisTemplate.delete(AUTH_CODE_PREFIX + email);
        redisTemplate.delete(AUTH_REQUEST_PREFIX + email);
    }

    // 인증 요청 여부 확인
    public boolean hasRequestedAuthCode(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(AUTH_REQUEST_PREFIX + email));
    }

}
