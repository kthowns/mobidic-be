package com.kthowns.mobidic.api.security.properties;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@ConfigurationProperties("jwt")
public record JwtProperties(
        String secret,
        Long exp
) {
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public long getJwtAccessExp() {
        return exp;
    }
}
