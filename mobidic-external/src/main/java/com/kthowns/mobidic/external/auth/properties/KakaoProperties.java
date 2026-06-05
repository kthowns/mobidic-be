package com.kthowns.mobidic.external.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(
        String restApiKey,
        String clientSecret,
        String backendCallbackUrl,
        DevProperties dev
) {
    public record DevProperties(String backendCallbackUrl) {
    }

    public String getBackendCallbackUrl(boolean isDev, String platform) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(isDev ? dev.backendCallbackUrl : backendCallbackUrl);

        if ("android".equals(platform)) {
            builder.queryParam("platform", "android");
        }

        if (isDev) {
            builder.queryParam("isDev", true);
        }

        return builder.toUriString();
    }
}
