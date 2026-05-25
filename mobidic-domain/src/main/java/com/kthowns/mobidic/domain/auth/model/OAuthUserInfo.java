package com.kthowns.mobidic.domain.auth.model;

import lombok.Builder;

@Builder
public record OAuthUserInfo(
        String socialId,
        String email,
        String nickname
) {
}
