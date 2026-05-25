package com.kthowns.mobidic.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private UUID id;
    private Long kakaoId;
    private String email;
    private String nickname;
    private String password;
    @Builder.Default
    private UserRole role = UserRole.USER;
    @Builder.Default
    private boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime deactivatedAt;
}
