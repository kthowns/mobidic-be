package com.kthowns.mobidic.domain.user.service;

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

    @Transactional
    public User registerUser(String email, String nickname, String password) {
        userValidator.validateEmailDuplication(email);
        userValidator.validateNicknameDuplication(nickname);
        userValidator.validatePassword(password);

        return userAppender.append(email, nickname, password, UserRole.USER);
    }

    @Transactional
    public User registerKakaoUser(Long kakaoId, String email, String nickname) {
        // 카카오 사용자는 이메일 중복 체크를 스킵하거나 별도 정책 필요
        return userAppender.appendKakao(
                kakaoId,
                email,
                nickname,
                UUID.randomUUID().toString(),
                UserRole.USER
        );
    }

    @Transactional
    public User updateUser(UUID userId, String nickname, String password) {
        if (nickname != null && !nickname.isEmpty()) {
            userValidator.validateNicknameUpdateDuplication(nickname, userId);
        }
        if (password != null && !password.isEmpty()) {
            userValidator.validatePassword(password);
        }

        return userUpdater.update(userId, nickname, password);
    }

    @Transactional
    public User deactivateUser(UUID userId) {
        return userRemover.deactivate(userId);
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userReader.readById(userId);
    }
}
