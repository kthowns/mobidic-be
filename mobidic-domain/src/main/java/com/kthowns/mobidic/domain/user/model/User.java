package com.kthowns.mobidic.domain.user.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record User(
        UUID id,
        Long kakaoId,
        String email,
        String nickname,
        String password,
        UserRole role,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime deactivatedAt
) {
}
