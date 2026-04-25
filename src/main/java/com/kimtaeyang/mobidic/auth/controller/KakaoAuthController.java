package com.kimtaeyang.mobidic.auth.controller;

import com.kimtaeyang.mobidic.auth.dto.KakaoLoginUrlResponse;
import com.kimtaeyang.mobidic.auth.dto.LoginResponse;
import com.kimtaeyang.mobidic.auth.service.KakaoAuthService;
import com.kimtaeyang.mobidic.auth.util.KakaoProperties;
import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.dto.GeneralResponse;
import com.kimtaeyang.mobidic.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;
    private final KakaoProperties kakaoProperties;
    private final UserFacade userFacade;

    @GetMapping("/login-url/kakao")
    public ResponseEntity<GeneralResponse<KakaoLoginUrlResponse>> getKakaoLoginUrl(
            @RequestParam(value = "isDev", defaultValue = "false") boolean isDev,
            @RequestParam(value = "platform", defaultValue = "web") String platform
    ) {
        KakaoLoginUrlResponse loginurl = kakaoAuthService.getKakaoLoginUrl(isDev, platform);

        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK, loginurl);
    }

    @GetMapping("/v1/oauth2/kakao")
    public RedirectView kakaoLogin(
            @RequestParam String code,
            @RequestParam(value = "isDev", defaultValue = "false") boolean isDev,
            @RequestParam(value = "platform", defaultValue = "web") String platform
    ) {
        final LoginResponse loginResponse = userFacade.kakaoLogin(code, isDev, platform);
        if (platform.equals("android")) {
            String baseAppSchemeUrl = kakaoProperties.getAppSchemeCallbackUrl();

            return new RedirectView(baseAppSchemeUrl + "?accessToken=" + loginResponse.getAccessToken());
        } else {
            String baseUrl = isDev ? kakaoProperties.getDevRedirectFrontendCallbackUrl()
                    : kakaoProperties.getRedirectFrontendCallbackUrl();

            return new RedirectView(baseUrl + "?accessToken=" + loginResponse.getAccessToken());
        }
    }
}
