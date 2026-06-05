package com.kthowns.mobidic.external.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth2.secrets.kakao")
public record KakaoApiProperties(
        String restApiKey,
        String clientSecret,
        DevProperties dev
) {
    public record DevProperties(String backendCallbackUrl) {
    }
}
