package com.kimtaeyang.mobidic.security;

import com.kimtaeyang.mobidic.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.kimtaeyang.mobidic.code.AuthResponseCode.INVALID_TOKEN;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private static final String LOGOUT_PREFIX = "logout:";
    private static final String WITHDRAWN_PREFIX = "withdraw:";
    @Value("${jwt.exp}")
    private Long exp;

    public void logoutToken(String token) {
        long remain = jwtUtil.getExpirationFromToken(token).getTime() - System.currentTimeMillis();

        if(isTokenLogout(token)) {
            throw new ApiException(INVALID_TOKEN);
        }

        redisTemplate.opsForValue().set(
                LOGOUT_PREFIX + token,
                "true",
                Duration.ofMillis(remain)
        );
    }

    public void withdrawToken(String token) {
        if(isTokenLogout(token)) {
            throw new ApiException(INVALID_TOKEN);
        }

        redisTemplate.opsForValue().set(
                WITHDRAWN_PREFIX + jwtUtil.getIdFromToken(token),
                "true",
                Duration.ofMillis(exp)
        );
    }

    public boolean isTokenLogout(String token) {
        return redisTemplate.hasKey(LOGOUT_PREFIX + token);
    }

    public boolean isTokenWithdrawn(String token) {
        return redisTemplate.hasKey(WITHDRAWN_PREFIX + jwtUtil.getIdFromToken(token));
    }
}