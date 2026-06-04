package com.kthowns.mobidic.domain.auth.client;

import com.kthowns.mobidic.domain.auth.model.OAuthUserInfo;

public interface OAuthClient {
    String getLoginUrl(boolean isDev, String platform);

    String getAccessToken(String authCode, boolean isDev, String platform);

    OAuthUserInfo getUserInfo(String accessToken);
}
