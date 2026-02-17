package com.kimtaeyang.mobidic.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtProperties {
    @Value("${jwt.secret}")
    private String JWT_SECRET;
    @Value("${jwt.exp}")
    private Long JWT_EXP;

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public long getJwtExp() {
        return JWT_EXP;
    }
}
