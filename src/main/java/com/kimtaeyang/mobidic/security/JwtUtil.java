package com.kimtaeyang.mobidic.security;

import com.kimtaeyang.mobidic.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;

    public String generateToken(UUID userId) {
        return Jwts.builder()
                    .subject(userId.toString())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + jwtProperties.getJwtExp()))
                    .signWith(jwtProperties.getSecretKey())
                    .compact();
    }

    public String generateTokenWithExp(UUID userId, Long exp) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    public String generateToken(UUID userId, @NonNull Function<JwtBuilder, JwtBuilder> func) {
        JwtBuilder builder = Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .signWith(jwtProperties.getSecretKey());

        return func.apply(builder).compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtProperties.getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            Date date = new Date();
            log.error("Invalid JWT token inspected : {} {}", e.getMessage(), date);
            return false;
        }
    }

    public UUID getIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtProperties.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return UUID.fromString(claims.getSubject());
    }

    public Date getExpirationFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtProperties.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration();
    }
}
