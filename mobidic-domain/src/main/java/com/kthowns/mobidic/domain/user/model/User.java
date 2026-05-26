package com.kthowns.mobidic.domain.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(
        UUID id,
        Long kakaoId,
        String email,
        String nickname,
        @JsonIgnore String password,
        UserRole role,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime deactivatedAt
) {
    public static User create(String email, String nickname, String password, UserRole role) {
        return new User(
                null,
                null,
                email,
                nickname,
                password,
                role,
                true,
                null,
                null
        );
    }

    public static User createKakao(Long kakaoId, String email, String nickname, String password, UserRole role) {
        return new User(
                null,
                kakaoId,
                email,
                nickname,
                password,
                role,
                true,
                null,
                null
        );
    }

    public User changeNickname(String newNickname) {
        return new User(
                this.id,
                this.kakaoId,
                this.email,
                newNickname,
                this.password,
                this.role,
                this.isActive,
                this.createdAt,
                this.deactivatedAt
        );
    }

    public User changePassword(String newPassword) {
        return new User(
                this.id,
                this.kakaoId,
                this.email,
                this.nickname,
                newPassword,
                this.role,
                this.isActive,
                this.createdAt,
                this.deactivatedAt
        );
    }

    public User deactivate() {
        return new User(
                this.id,
                this.kakaoId,
                this.email,
                this.nickname,
                this.password,
                this.role,
                false,
                this.createdAt,
                LocalDateTime.now()
        );
    }
}
