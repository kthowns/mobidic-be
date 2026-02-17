package com.kimtaeyang.mobidic.security;

import com.kimtaeyang.mobidic.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
class JwtUtilTest {
    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtUtil jwtUtil;

    private final long testExp = 10000L;
    private final String testKey = UUID.randomUUID().toString();

    @DisplayName("[Security][JWT] Generate Token Success")
    @Test
    void generateTokenTestSuccess() {
        UUID uid = UUID.randomUUID();

        //given
        given(jwtProperties.getJwtExp())
                .willReturn(testExp);
        given(jwtProperties.getSecretKey())
                .willReturn(Keys.hmacShaKeyFor(testKey.getBytes(StandardCharsets.UTF_8)));

        //when
        String token = jwtUtil.generateToken(uid);
        Claims claims = Jwts.parser()
                .verifyWith(jwtProperties.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        //then
        assertEquals(uid, UUID.fromString(claims.getSubject()));
    }

    @DisplayName("[Security][JWT] Validate Token Success")
    @Test
    void validateTokenSuccess() {
        UUID uid = UUID.randomUUID();

        //given
        given(jwtProperties.getJwtExp())
                .willReturn(testExp);
        given(jwtProperties.getSecretKey())
                .willReturn(Keys.hmacShaKeyFor(testKey.getBytes(StandardCharsets.UTF_8)));
        String token = jwtUtil.generateToken(uid);
        //when
        boolean isValid = jwtUtil.validateToken(token);
        //then
        assertThat(isValid);
    }

    @DisplayName("[Security][JWT] Validate Token Fail")
    @Test
    void validateTokenFail() {
        UUID uid = UUID.randomUUID();

        //given
        given(jwtProperties.getJwtExp())
                .willReturn(1L);
        given(jwtProperties.getSecretKey())
                .willReturn(Keys.hmacShaKeyFor(testKey.getBytes(StandardCharsets.UTF_8)));
        String token = jwtUtil.generateToken(uid);

        //when
        boolean isValid = jwtUtil.validateToken(token);

        //then
        assertThat(!isValid);
    }

    @DisplayName("[Security][JWT] Get id from Token Success")
    @Test
    void getIdFromTokenTestSuccess() {
        UUID uuid = UUID.randomUUID();

        //given
        given(jwtProperties.getJwtExp())
                .willReturn(testExp);
        given(jwtProperties.getSecretKey())
                .willReturn(Keys.hmacShaKeyFor(testKey.getBytes(StandardCharsets.UTF_8)));
        String token = jwtUtil.generateToken(uuid);

        //when
        UUID resultId = jwtUtil.getIdFromToken(token);

        //then
        assertEquals(uuid, resultId);
    }
}