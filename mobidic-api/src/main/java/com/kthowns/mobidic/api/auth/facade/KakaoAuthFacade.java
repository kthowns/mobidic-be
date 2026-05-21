package com.kthowns.mobidic.api.auth.facade;

import com.kthowns.mobidic.api.auth.dto.response.KakaoUserInfo;
import com.kthowns.mobidic.api.auth.dto.response.LoginResponse;
import com.kthowns.mobidic.api.auth.model.AuthUser;
import com.kthowns.mobidic.api.auth.service.KakaoAuthService;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.domain.preset.service.PresetVocabularyService;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.service.UserService;
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

        AuthUser authUser = transactionTemplate.execute((status) -> getOrCreateUser(kakaoUserInfo));

        return LoginResponse.builder()
                .accessToken(jwtProvider.generateToken(authUser.getId(), authUser.getRole()))
                .build();
    }

    private AuthUser getOrCreateUser(KakaoUserInfo kakaoUserInfo) {
        return kakaoAuthService.getUserByKakaoId(kakaoUserInfo.getId())
                .orElseGet(() -> {
                    User user = userService.registerKakaoUser(
                            kakaoUserInfo.getId(),
                            kakaoUserInfo.getKakaoAccount().getEmail(),
                            kakaoUserInfo.getKakaoAccount().getProfile().getNickname()
                    );
                    presetVocabularyService.copyAllPresetToUser(user.getId());

                    return kakaoAuthService.getUserByKakaoId(kakaoUserInfo.getId()).get();
                });
    }
}
