package com.kimtaeyang.mobidic.security.jwt;

import com.kimtaeyang.mobidic.security.service.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public String generateToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getJwtExp()))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String userId = claims.getSubject();
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUserId(UUID.fromString(userId));
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtProperties.getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }

    public UUID getIdFromToken(String token) {
        Claims claims = getClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    public Date getExpirationFromToken(String token) {
        return getClaims(token).getExpiration();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtProperties.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
