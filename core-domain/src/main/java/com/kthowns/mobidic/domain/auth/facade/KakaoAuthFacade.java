package com.kthowns.mobidic.domain.auth.facade;

import com.kthowns.mobidic.domain.auth.model.OAuthUserInfo;
import com.kthowns.mobidic.domain.auth.service.KakaoAuthService;
import com.kthowns.mobidic.domain.preset.service.PresetVocabularyService;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class KakaoAuthFacade {
    private final KakaoAuthService kakaoAuthService;
    private final UserService userService;
    private final PresetVocabularyService presetVocabularyService;

    @Transactional
    public User kakaoLogin(String authCode, boolean isDev, String platform, String serverBaseUrl) {
        String accessToken = kakaoAuthService.getKakaoAccessToken(authCode, isDev, platform, serverBaseUrl);
        OAuthUserInfo oauthUserInfo = kakaoAuthService.getKakaoUserInfo(accessToken);

        return getOrCreateUser(oauthUserInfo);
    }

    private User getOrCreateUser(OAuthUserInfo oauthUserInfo) {
        return kakaoAuthService.getUserByKakaoId(Long.parseLong(oauthUserInfo.socialId()))
                .orElseGet(() -> {
                    User user = userService.registerKakaoUser(
                            Long.parseLong(oauthUserInfo.socialId()),
                            oauthUserInfo.email(),
                            oauthUserInfo.nickname()
                    );
                    presetVocabularyService.copyAllPresetToUser(user.id());

                    return user;
                });
    }
}
