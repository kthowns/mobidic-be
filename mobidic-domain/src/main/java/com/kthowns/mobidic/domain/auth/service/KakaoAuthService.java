package com.kthowns.mobidic.domain.auth.service;

import com.kthowns.mobidic.domain.auth.client.OAuthClient;
import com.kthowns.mobidic.domain.auth.model.OAuthUserInfo;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> getUserByKakaoId(Long kakaoId) {
        return userRepository.readByKakaoId(kakaoId);
    }

    public String getKakaoLoginUrl(boolean isDev, String platform) {
        return oauthClient.getLoginUrl(isDev, platform);
    }

    public OAuthUserInfo getKakaoUserInfo(String kakaoAccessToken) {
        return oauthClient.getUserInfo(kakaoAccessToken);
    }

    public String getKakaoAccessToken(String code, boolean isDev, String platform) {
        return oauthClient.getAccessToken(code, isDev, platform);
    }
}
