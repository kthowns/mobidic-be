package com.kthowns.mobidic.domain.auth.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserInfo {
    private String socialId;
    private String email;
    private String nickname;
}
