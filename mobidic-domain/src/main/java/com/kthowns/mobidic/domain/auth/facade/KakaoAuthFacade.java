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
    public User kakaoLogin(String authCode, boolean isDev, String platform) {
        String accessToken = kakaoAuthService.getKakaoAccessToken(authCode, isDev, platform);
        OAuthUserInfo oauthUserInfo = kakaoAuthService.getKakaoUserInfo(accessToken);

        return getOrCreateUser(oauthUserInfo);
    }

    private User getOrCreateUser(OAuthUserInfo oauthUserInfo) {
        return kakaoAuthService.getUserByKakaoId(Long.parseLong(oauthUserInfo.getSocialId()))
                .orElseGet(() -> {
                    User user = userService.registerKakaoUser(
                            Long.parseLong(oauthUserInfo.getSocialId()),
                            oauthUserInfo.getEmail(),
                            oauthUserInfo.getNickname()
                    );
                    presetVocabularyService.copyAllPresetToUser(user.getId());

                    return user;
                });
    }
}
