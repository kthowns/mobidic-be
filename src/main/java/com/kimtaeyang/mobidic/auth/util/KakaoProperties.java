package com.kimtaeyang.mobidic.auth.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KakaoProperties {
    @Value("${kakao.rest-api-key}")
    private String clientId;
    @Value("${kakao.redirect-url}")
    private String redirectUrl;
    @Value("${kakao.client-secret}")
    private String clientSecret;
    @Value("${kakao.redirect-frontend-callback-url}")
    private String redirectFrontendCallbackUrl;

    @Value("${kakao.dev.redirect-frontend-callback-url}")
    private String devRedirectFrontendCallbackUrl;
    @Value("${kakao.dev.redirect-url}")
    private String devRedirectUrl;
}