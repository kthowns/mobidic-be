package com.kthowns.mobidic.domain.user.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class UserValidator {
    private final UserRepository userRepository;

    public void validateEmailDuplication(String email) {
        if (email == null || userRepository.existsByEmail(email)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_EMAIL);
        }
    }

    public void validateNicknameDuplication(String nickname) {
        if (nickname == null || userRepository.existsByNickname(nickname)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_NICKNAME);
        }
    }

    public void validateNicknameUpdateDuplication(String nickname, UUID userId) {
        if (nickname == null || userRepository.existsByNicknameAndIdNot(nickname, userId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_NICKNAME);
        }
    }

    public void validatePassword(String plainPassword) {
        if (plainPassword == null || plainPassword.length() < 8) {
            throw new ApiException(GeneralResponseCode.INVALID_REQUEST, "비밀번호는 8자 이상이어야 합니다.");
        }
    }
}
