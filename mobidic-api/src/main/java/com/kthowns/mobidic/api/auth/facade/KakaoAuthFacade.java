package com.kthowns.mobidic.api.auth.facade;

import com.kthowns.mobidic.api.dto.common.auth.KakaoUserInfo;
import com.kthowns.mobidic.api.dto.response.auth.LoginResponse;
import com.kthowns.mobidic.api.auth.service.KakaoAuthService;
import com.kthowns.mobidic.api.preset.service.PresetVocabularyService;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.user.entity.User;
import com.kthowns.mobidic.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@RequiredArgsConstructor
public class KakaoAuthFacade {
    private final KakaoAuthService kakaoAuthService;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final TransactionTemplate transactionTemplate;
    private final PresetVocabularyService presetVocabularyService;

    public LoginResponse kakaoLogin(String authCode, boolean isDev, String platform) {
        String accessToken = kakaoAuthService.getKakaoAccessToken(authCode, isDev, platform);
        KakaoUserInfo kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(accessToken);

        User user = transactionTemplate.execute((status) -> getOrCreateUser(kakaoUserInfo));

        return LoginResponse.builder()
                .accessToken(jwtProvider.generateToken(user.getId()))
                .build();
    }

    private User getOrCreateUser(KakaoUserInfo kakaoUserInfo) {
        return kakaoAuthService.getUserByKakaoId(kakaoUserInfo.getId())
                .orElseGet(() -> {
                    User u = userService.registerKakaoUser(kakaoUserInfo);
                    presetVocabularyService.copyAllPresetToUser(u);

                    return u;
                });
    }
}
