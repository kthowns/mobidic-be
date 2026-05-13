package com.kthowns.mobidic.auth.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Getter
public class KakaoProperties {
    @Value("${kakao.rest-api-key}")
    private String clientId;
    @Value("${kakao.client-secret}")
    private String clientSecret;
    @Value("${kakao.backend-callback-url}")
    private String backendCallbackUrl;
    @Value("${kakao.frontend-callback-url}")
    private String frontendCallbackUrl;
    @Value("${kakao.android-callback-url}")
    private String androidCallbackUrl;

    @Value("${kakao.dev.frontend-callback-url}")
    private String devFrontendCallbackUrl;
    @Value("${kakao.dev.backend-callback-url}")
    private String devBackendCallbackUrl;

    public String getBackendCallbackUrl(boolean isDev, String platform) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(isDev ? devBackendCallbackUrl : backendCallbackUrl);

        if (platform.equals("android")) {
            builder.queryParam("platform", "android");
        }

        if (isDev) {
            builder.queryParam("isDev", true);
        }

        return builder.toUriString();
    }

    public String getFrontendCallbackUrl(boolean isDev, String platform) {
        if (platform.equals("android")) {
            return androidCallbackUrl;
        } else {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(isDev ? devFrontendCallbackUrl : frontendCallbackUrl);

            return builder.toUriString();
        }
    }
}