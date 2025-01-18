package com.example.bookjourneybackend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final RedisTemplate<String, Object> redisTemplate;

    public void storeRefreshToken(String token, Long userId) {
        // redis 에 토큰 저장
        log.info("[TokenService.storeRefreshToken]");
        redisTemplate.opsForValue().set(String.valueOf(userId), token, refreshTokenExpiration, TimeUnit.MILLISECONDS);
    }

    public boolean checkTokenExists(String userId) {
        // 주어진 userId로 저장된 토큰이 Redis에 존재하는지 확인
        Boolean result = redisTemplate.hasKey(userId);
        return result != null && result;
    }

    public void invalidateToken(Long userId) {
        // redis 에서 토큰 삭제
        redisTemplate.delete(String.valueOf(userId));
    }
}
