package com.kthowns.mobidic.api.auth.controller;

import com.kthowns.mobidic.api.auth.dto.response.KakaoLoginUrlResponse;
import com.kthowns.mobidic.api.auth.properties.OAuthKakaoCallbackUrlProperties;
import com.kthowns.mobidic.api.global.dto.GeneralResponse;
import com.kthowns.mobidic.security.util.JwtProvider;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.auth.facade.KakaoAuthFacade;
import com.kthowns.mobidic.domain.auth.service.KakaoAuthService;
import com.kthowns.mobidic.domain.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
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
    private final OAuthKakaoCallbackUrlProperties kakaoCallbackUrlProperties;
    private final KakaoAuthFacade kakaoAuthFacade;
    private final JwtProvider jwtProvider;

    @GetMapping("/login-url/kakao")
    public ResponseEntity<GeneralResponse<KakaoLoginUrlResponse>> getKakaoLoginUrl(
            @RequestParam(value = "isDev", defaultValue = "false") boolean isDev,
            @RequestParam(value = "platform", defaultValue = "web") String platform,
            HttpServletRequest request
    ) {
        final String serverBaseUrl = getServerBaseUrl(request);
        final String url = kakaoAuthService.getKakaoLoginUrl(isDev, platform, serverBaseUrl);
        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK, new KakaoLoginUrlResponse(url));
    }

    @GetMapping("/v1/oauth2/kakao")
    public RedirectView kakaoLogin(
            @RequestParam String code,
            @RequestParam(value = "isDev", defaultValue = "false") boolean isDev,
            @RequestParam(value = "platform", defaultValue = "web") String platform,
            HttpServletRequest request
    ) {
        final String serverBaseUrl = getServerBaseUrl(request);
        final User user = kakaoAuthFacade.kakaoLogin(code, isDev, platform, serverBaseUrl);
        final String accessToken = jwtProvider.generateToken(user.id(), user.role().name());
        final String redirectUrl = kakaoCallbackUrlProperties.getFrontendCallbackUrl(isDev, platform);

        return new RedirectView(redirectUrl + "?access_token=" + accessToken);
    }

    private String getServerBaseUrl(HttpServletRequest request) {
        String serverBaseUrl = request.getScheme() + "://" + request.getServerName();

        // 로컬 개발 환경처럼 포트가 80이나 443이 아닐 때만 포트 번호를 붙여주는 예외 처리
        int port = request.getServerPort();
        if (port != 80 && port != 443) {
            serverBaseUrl += ":" + port;
        }
        log.info("Server Base URL : {}", serverBaseUrl);

        return serverBaseUrl;
    }
}
