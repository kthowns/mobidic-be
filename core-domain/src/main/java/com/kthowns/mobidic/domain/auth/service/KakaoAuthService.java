package com.kthowns.mobidic.domain.auth.service;

import com.kthowns.mobidic.domain.auth.client.OAuthClient;
import com.kthowns.mobidic.domain.auth.model.OAuthUserInfo;
import com.kthowns.mobidic.domain.auth.repository.AuthUserRepository;
import com.kthowns.mobidic.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {
    private final OAuthClient oauthClient;
    private final AuthUserRepository authUserRepository;

    @Transactional(readOnly = true)
    public Optional<User> getUserByKakaoId(Long kakaoId) {
        return authUserRepository.readByKakaoId(kakaoId);
    }

    public String getKakaoLoginUrl(boolean isDev, String platform, String serverBaseUrl) {
        return oauthClient.getLoginUrl(isDev, platform, serverBaseUrl);
    }

    public OAuthUserInfo getKakaoUserInfo(String kakaoAccessToken) {
        return oauthClient.getUserInfo(kakaoAccessToken);
    }

    public String getKakaoAccessToken(String code, boolean isDev, String platform, String serverBaseUrl) {
        return oauthClient.getAccessToken(code, isDev, platform, serverBaseUrl);
    }
}
