package com.kthowns.mobidic.api.security.jwt;

import com.kthowns.mobidic.api.auth.model.AuthUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(UUID userId, String userRole) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", userRole)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getJwtExp()))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);

        AuthUser authUser = AuthUser.builder()
                .id(UUID.fromString(userId))
                .role(role)
                .build();

        return new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtProperties.getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e.getMessage());
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
