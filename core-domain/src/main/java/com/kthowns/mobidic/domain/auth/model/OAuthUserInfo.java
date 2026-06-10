package com.kthowns.mobidic.domain.auth.model;

public record OAuthUserInfo(
        String socialId,
        String email,
        String nickname
) {
    public static OAuthUserInfo of(String socialId, String email, String nickname) {
        return new OAuthUserInfo(socialId, email, nickname);
    }
}
