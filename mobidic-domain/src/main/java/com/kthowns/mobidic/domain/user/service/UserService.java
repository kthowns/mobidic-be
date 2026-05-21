package com.kthowns.mobidic.domain.user.service;

import com.kthowns.mobidic.domain.user.client.PasswordEncoderClient;
import com.kthowns.mobidic.domain.user.implementation.*;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserReader userReader;
    private final UserAppender userAppender;
    private final UserUpdater userUpdater;
    private final UserRemover userRemover;
    private final UserValidator userValidator;

    private final PasswordEncoderClient passwordEncoderClient;

    @Transactional
    public User registerUser(String email, String nickname, String password) {
        userValidator.validateEmailDuplication(email);
        userValidator.validateNicknameDuplication(nickname);

        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .password(passwordEncoderClient.encode(password))
                .role(UserRole.USER)
                .isActive(true)
                .build();

        return userAppender.append(user);
    }

    @Transactional
    public User registerKakaoUser(Long kakaoId, String email, String nickname) {
        // 카카오 사용자는 이메일 중복 체크를 스킵하거나 별도 정책 필요
        User user = User.builder()
                .kakaoId(kakaoId)
                .email(email)
                .nickname(nickname)
                .password(passwordEncoderClient.encode(UUID.randomUUID().toString()))
                .role(UserRole.USER)
                .isActive(true)
                .build();

        return userAppender.append(user);
    }

    @Transactional
    public User updateUser(UUID userId, String nickname, String password) {
        User user = userReader.readById(userId);

        if (nickname != null) {
            userValidator.validateNicknameUpdateDuplication(nickname, userId);
        }

        User updatedUser = User.builder()
                .id(user.getId())
                .kakaoId(user.getKakaoId())
                .email(user.getEmail())
                .nickname(nickname != null ? nickname : user.getNickname())
                .password(password != null ? passwordEncoderClient.encode(password) : user.getPassword())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();

        return userUpdater.update(updatedUser);
    }

    @Transactional
    public User deactivateUser(UUID userId) {
        User user = userReader.readById(userId);
        return userRemover.deactivate(user);
    }
}
