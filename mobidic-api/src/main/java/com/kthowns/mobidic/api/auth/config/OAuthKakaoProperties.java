package com.kthowns.mobidic.api.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

@ConfigurationProperties("oauth2.kakao")
public record OAuthKakaoProperties(
        String frontendCallbackUrl,
        String androidCallbackUrl,
        DevProperties dev
) {
    public record DevProperties(
            String frontendCallbackUrl
    ) {
    }

    public String getFrontendCallbackUrl(boolean isDev, String platform) {
        if ("android".equals(platform)) {
            return androidCallbackUrl;
        } else {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(isDev ? dev.frontendCallbackUrl : frontendCallbackUrl);

            return builder.toUriString();
        }
    }
}
