package com.kthowns.mobidic.domain.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kthowns.mobidic.domain.global.model.AuditTime;

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
        AuditTime auditTime,
        LocalDateTime deactivatedAt
) {
    public static User create(String email, String nickname, String password, UserRole role) {
        // TODO: UserRole 파라미터 제거하고 Role 별 사용자 생성 팩토리 메서드 분리하기

        return new User(
                null,
                null,
                email,
                nickname,
                password,
                role,
                true,
                AuditTime.create(),
                null
        );
    }

    public static User createKakao(Long kakaoId, String email, String nickname, String password, UserRole role) {
        // TODO: UserRole 파라미터 제거하고 Role 별 사용자 생성 팩토리 메서드 분리하기

        return new User(
                null,
                kakaoId,
                email,
                nickname,
                password,
                role,
                true,
                AuditTime.create(),
                null
        );
    }

    public User updateProfile(String newNickname, String newPassword) {
        return new User(
                this.id,
                this.kakaoId,
                this.email,
                newNickname,
                newPassword,
                this.role,
                this.isActive,
                AuditTime.update(this.auditTime),
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
                AuditTime.update(this.auditTime),
                LocalDateTime.now()
        );
    }
}
