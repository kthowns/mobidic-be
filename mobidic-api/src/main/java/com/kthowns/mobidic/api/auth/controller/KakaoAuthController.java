package com.kthowns.mobidic.api.auth.controller;

import com.kthowns.mobidic.api.auth.dto.KakaoLoginUrlResponse;
import com.kthowns.mobidic.api.auth.dto.LoginResponse;
import com.kthowns.mobidic.api.auth.facade.KakaoAuthFacade;
import com.kthowns.mobidic.api.auth.service.KakaoAuthService;
import com.kthowns.mobidic.api.auth.util.KakaoProperties;
import com.kthowns.mobidic.api.common.code.GeneralResponseCode;
import com.kthowns.mobidic.api.common.dto.GeneralResponse;
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
    private final KakaoAuthFacade kakaoAuthFacade;

    @GetMapping("/login-url/kakao")
    public ResponseEntity<GeneralResponse<KakaoLoginUrlResponse>> getKakaoLoginUrl(
            @RequestParam(value = "isDev", defaultValue = "false") boolean isDev,
            @RequestParam(value = "platform", defaultValue = "web") String platform
    ) {
        final KakaoLoginUrlResponse response = kakaoAuthService.getKakaoLoginUrl(isDev, platform);

        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK, response);
    }

    @GetMapping("/v1/oauth2/kakao")
    public RedirectView kakaoLogin(
            @RequestParam String code,
            @RequestParam(value = "isDev", defaultValue = "false") boolean isDev,
            @RequestParam(value = "platform", defaultValue = "web") String platform
    ) {
        final LoginResponse loginResponse = kakaoAuthFacade.kakaoLogin(code, isDev, platform);
        final String accessToken = loginResponse.getAccessToken();
        final String redirectUrl = kakaoProperties.getFrontendCallbackUrl(isDev, platform);

        return new RedirectView(redirectUrl + "?access_token=" + accessToken);
    }
}
